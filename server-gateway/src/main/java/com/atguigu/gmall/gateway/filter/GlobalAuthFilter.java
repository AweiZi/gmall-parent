package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.properties.AuthUrlProperties;
import com.atguigu.gmall.model.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Webflux：响应式web编程  消息队列分布式
 * 内存版的消息队列
 * //1
 * Future fu = aService.b();
 * //2
 * fu.get();
 */
@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter {

    AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    AuthUrlProperties urlProperties;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 责任链模式
     * class FilterChain {
     * Filter[] filters;
     * int index = 0;
     * <p>
     * 1.第一种写法
     * doFilter(req,resp){
     * for(Filter filter:filters)
     * filter.doFilter(req,resp,chain)
     * }
     * //2、第二种写法
     * doFilter(req,resp,chain){
     * filters[index++].doFilter(req,resp,this);
     * }
     * }
     *
     * @param exchange 有请求和响应
     */

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取请求路径 /order/xxx
        String path = exchange.getRequest().getURI().getPath();
        String uri = exchange.getRequest().getURI().toString();
        log.info("{}请求开始：", path);

        //2.无需登录就能访问的资源
        for (String url : urlProperties.getNoAuthUrl()) {
            boolean match = matcher.match(url, path);
            if (match) {
                return chain.filter(exchange);
            }
        }
        //静态资源虽然带了token。不用校验token，直接放

        //能走到这说明不是直接放行的资源
        //3.只要是api/inner/的全部拒绝
        for (String url : urlProperties.getDenyUrl()) {
            boolean match = matcher.match(url, path);
            if (match) {
                //直接响应json数据即可
                Result<String> result = Result.build("",
                        ResultCodeEnum.PERMISSION);
                return responseResult(result, exchange);
            }
        }
        //4.需要登录的请求，进行权限验证
        for (String url : urlProperties.getLoginAuthUrl()) {
            boolean match = matcher.match(url, path);
            if (match) {
                //登录等校验
                //3.1获取token信息信息【Cookie[token=xxx]】【Header[token=xxx]】
                String tokenValue = getTokenValue(exchange);
                //3.2验证token
                UserInfo info = getTokenUserInfo(tokenValue);
                //3.3判断用户信息是否正确
                if (info != null) {
                    //Redis中有此用户。exchange里面的request的头会新增一个userid
                    ServerWebExchange webExchange = userIdTransport(info, exchange);
                    return chain.filter(webExchange);
                } else {
                    //redis中无此用户【假令牌 token没有  没登录
                    //重定向到登录页
                    return redirectToCustomPage(urlProperties.getLoginPage() + "?originUrl=" + uri, exchange);
                }
            }
        }

        /**
         * 能走到这，既不是静态资源直接放行，也不是必须登录才能访问的，就一普通请求
         * 普通请求只要带了token 说明可能登陆了 只要登陆了  就透传用户id
         */
        String tokenValue = getTokenValue(exchange);
        UserInfo info = getTokenUserInfo(tokenValue);
        //如果前端带了token 还是没有用户信息 代表这是假令牌
        if (!StringUtils.isEmpty(tokenValue) && info == null) {
            //假请求直接打回登录
            return redirectToCustomPage(urlProperties.getLoginPage() + "?originUrl=" + uri, exchange);
        }
        //普通请求，透传用户id或者临时id
        exchange = userIdOrTempIdTransport(info, exchange);
        return chain.filter(exchange);

        //4.对登录后的请求进行user_id透传
        //        Mono<Void> filter = chain.filter(exchange)
        //                .doFinally(signalType -> {
        //                    log.info("{}请求结束", path);
        //                });
        //
    }

    /**
     * 用户id透传
     * 再看看前端有没有带临时id，如果带了顺便透传一下
     */
    private ServerWebExchange userIdOrTempIdTransport(UserInfo info, ServerWebExchange exchange) {
        //请求一旦发来 所有的请求数据都是固定的，不能进行任何修改 只能读取
        ServerHttpRequest.Builder newRequilder = exchange.getRequest().mutate();

        //用户登陆了
        if (info != null) {
            newRequilder.header(SysRedisConst.USERID_HEADER, info.getId().toString());
        }
        //用户没登陆
        String userTempId = getUserTempId(exchange);
        newRequilder.header(SysRedisConst.USERTEMPID_HEADER, userTempId);

        //放行的时候传改掉的exchange
        ServerWebExchange webExchange = exchange.mutate()
                .request(newRequilder.build())
                .response(exchange.getResponse())
                .build();

        return webExchange;
    }

    /**
     * 获取临时id
     */
    private String getUserTempId(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        //1、尝试获取头中的临时id
        String tempId = request.getHeaders().getFirst("userTempId");
        //2.如果头中没有，尝试获取cookie的值
        if (StringUtils.isEmpty(tempId)) {
            HttpCookie httpCookie = request.getCookies().getFirst("userTempId");
            if (httpCookie != null) {
                tempId = httpCookie.getValue();
            }
        }
        return tempId;
    }

    /**
     * 重定向到指定位置
     */
    private Mono<Void> redirectToCustomPage(String location, ServerWebExchange exchange) {
        //获取响应头
        ServerHttpResponse response = exchange.getResponse();

        //1、重定向【302状态码 + 响应头中 Location: 新位置】
        response.setStatusCode(HttpStatus.FOUND);
        // http://passport.gmall.com/login.html?originUrl=http://gmall.com/
        response.getHeaders().add(HttpHeaders.LOCATION, location);

        //2.清除旧的错误的Cookie[token]（同名cookie并max-age=0）解决无限重定向问题
        ResponseCookie tokenCookie = ResponseCookie
                .from("token", "777")//重新设置一个token
                .maxAge(0)//设置过期事件
                .path("/")//设置path路径
                .domain(".gmall.com")//设置cookie的属性
                .build();
        response.getCookies().set("token", tokenCookie);
        //响应结束
        return response.setComplete();
    }

    /**
     * 用户id透传
     */
    private ServerWebExchange userIdTransport(UserInfo info, ServerWebExchange exchange) {
        if (info != null) {
            //请求一旦发来，所有的请求数据都是固定的，不能进行任何修改，只能读取
            ServerHttpRequest request = exchange.getRequest();

            //根据原来的请求 封装有一个新请求
            ServerHttpRequest newReq = exchange.getRequest()
                    .mutate()//变成一个新的
                    .header(SysRedisConst.USERID_HEADER, info.getId().toString())
                    .build();//添加自己的请求头

            //放行的时候传改掉的exchange
            ServerWebExchange webExchange = exchange
                    .mutate()
                    .request(newReq)
                    .response(exchange.getResponse())
                    .build();
            return webExchange;
        }
        return exchange;
    }

    /**
     * 根据tokenValue从redis中查询
     */
    private UserInfo getTokenUserInfo(String tokenValue) {
        String json = redisTemplate.opsForValue().get(SysRedisConst.LOGIN_USER + tokenValue);
        if (!StringUtils.isEmpty(json)) {
            return Jsons.toObj(json, UserInfo.class);
        }
        return null;
    }

    /**
     * 从cookie或请求头中取到token对应的值
     */
    private String getTokenValue(ServerWebExchange exchange) {
        //由于前端乱写，到处可能都有【Cookie[token=xxx]】【Header[token=xxx]】
        //1.先检查cookie中有没有这个token
        String tokenValue = "";
        HttpCookie token = exchange.getRequest().getCookies().getFirst("token");
        if (token != null) {
            //如果有从浏览器中获取token的值
            tokenValue = token.getValue();
            return tokenValue;
        }
        //2.走到这，说明cookie中没有
        tokenValue = exchange.getRequest().getHeaders().getFirst("token");
        return tokenValue;
    }

    /**
     * 响应一个结果
     */
    private Mono<Void> responseResult(Result<String> result, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.OK);
        String jsonStr = Jsons.toStr(result);

        //DataBffer
        DataBuffer dataBuffer = response.bufferFactory()
                .wrap(jsonStr.getBytes());
        //设置返回类型
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(dataBuffer));
    }
}

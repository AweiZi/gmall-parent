package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuProductFeignClient skuFeignClient;

    @Override
    public SkuInfo addToCart(Long skuId, Integer num) {
        //  cart:user: == hash(skuId,skuInfo)
        //1.决定购物车使用哪个键
        String cartKey = determinCartKey();

        //2.给购物车添加指定商品
        SkuInfo skuInfo = addItemToCart(skuId, num, cartKey);

        return null;
    }

    @Override
    public SkuInfo addItemToCart(Long skuId, Integer num, String cartKey) {
        //拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);

        Boolean hasKey = cart.hasKey(skuId.toString());
        //获取当前购物车的品类数量
        Long itemsSize = cart.size();
        //1.如果这个skuid之前没有添加过，就新增 还需要远程调用当前信息
        if (!hasKey) {
            if (itemsSize + 1 > SysRedisConst.CART_ITEMS_LIMIT) {
                //异常机制
                throw new GmallException(ResultCodeEnum.CART_OVERFLOW);
            }
            //1.1远程获取商品信息
            SkuInfo data = skuFeignClient.getSkuInfo(skuId).getData();
            //1.2转为购物车中要保存的数据类型
            CartInfo item = converSkuInfo2CardInfo(data);
            //            Result<BigDecimal> price = skuFeignClient.getSku1010Price(skuId);
            item.setSkuNum(num);
            //            item.setSkuPrice(price.getData()); //设置好实时价格
            //1.3给redis保存起来
            cart.put(skuId.toString(), Jsons.toStr(item));
            return data;
        } else {
            //2.如果这个skuid之前添加过，就修改skuid对应的商品的数量
            //2.1获取实时价格
            BigDecimal price = skuFeignClient.getSku1010Price(skuId).getData();
            //2.2获取原来的信息
            CartInfo cartInfo = getItemFromCart(cartKey, skuId);
            //2.3更新商品
            cartInfo.setSkuPrice(price);
            cartInfo.setSkuNum(cartInfo.getSkuNum() + num);
            cartInfo.setUpdateTime(new Date());
            //2.4同步到redis
            cart.put(skuId.toString(), Jsons.toStr(cartInfo));
            SkuInfo skuInfo = converCartInfo2SkuInfo(cartInfo);
            return skuInfo;
        }
    }

    /**
     * 将cartinfo转为skuinfo
     */
    private SkuInfo converCartInfo2SkuInfo(CartInfo cartInfo) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSkuName(cartInfo.getSkuName());
        skuInfo.setSkuDefaultImg(cartInfo.getImgUrl());
        skuInfo.setId(cartInfo.getSkuId());

        return skuInfo;
    }

    @Override
    public CartInfo getItemFromCart(String cartKey, Long skuId) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(cartKey);
        //1.拿到购物车中指定商品的json数据
        String jsonData = ops.get(skuId.toString());

        return Jsons.toObj(jsonData, CartInfo.class);
    }

    @Override
    public List<CartInfo> getCartList(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //流式编程
        List<CartInfo> infos = hashOps.values()
                .stream()
                .map(str -> Jsons.toObj(str, CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());
        return infos;
    }

    @Override
    public void updateItemNum(Long skuId, Integer num, String cartKey) {
        //1、拿到购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        //2.拿到商品
        CartInfo item = getItemFromCart(cartKey, skuId);
        item.setSkuNum(item.getSkuNum() + num);
        item.setUpdateTime(new Date());
        //3.保存到购物车
        hashOps.put(skuId.toString(), Jsons.toStr(item));
    }

    @Override
    public void updateChecked(Long skuId, Integer status, String cartKey) {
        //1、拿到购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //2.拿到要修改的商品
        CartInfo item = getItemFromCart(cartKey, skuId);
        item.setIsChecked(status);
        item.setUpdateTime(new Date());
        //3.保存
        hashOps.put(skuId.toString(), Jsons.toStr(item));
    }

    @Override
    public void deleteCartItem(Long skuId, String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        hashOps.delete(skuId.toString());

    }

    @Override
    public void deleteChecked(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        //1.拿到选中的商品，并删除。收集所有选中的商品id
        List<String> ids = getCheckedItems(cartKey)
                .stream()
                .map(cartInfo -> cartInfo.getSkuId().toString())
                .collect(Collectors.toList());
        if (ids != null && ids.size() > 0) {
            hashOps.delete(ids.toArray());
        }
    }

    @Override
    public List<CartInfo> getCheckedItems(String cartKey) {
        List<CartInfo> cartList = getCartList(cartKey);
        List<CartInfo> checkedItems = cartList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == null)
                .collect(Collectors.toList());
        return checkedItems;
    }

    @Override
    public void mergeUserAndTempCart() {
        //得到临时id或者当前用户id
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        //1.判断是否需要合并
        if (authInfo.getUserId() != null && !StringUtils.isEmpty(authInfo.getUserTempId())) {
            //2.可能需要合并
            //3.临时购物车有东西  合并后删除临时购物车
            String tempCartKey = SysRedisConst.CART_KEY + authInfo.getUserTempId();
            //3.1获取临时购物车所有的商品
            List<CartInfo> tempCartList = getCartList(tempCartKey);
            if (tempCartList != null && tempCartList.size() > 0) {
                //临时购物车有数据，需要合并
                String userCartKey = SysRedisConst.CART_KEY+authInfo.getUserId();
                for (CartInfo info : tempCartList) {
                    Long skuId = info.getSkuId();
                    Integer skuNum = info.getSkuNum();
                    addItemToCart(skuId,skuNum,userCartKey);
                    //3.2合并成一个商品就删除一个
                    redisTemplate.opsForHash().delete(tempCartKey,skuId.toString());
                }
            }
        }
    }

    /**
     * 把skuinfo转为cartInfo
     */
    private CartInfo converSkuInfo2CardInfo(SkuInfo data) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(data.getId());
        cartInfo.setImgUrl(data.getSkuDefaultImg());
        cartInfo.setSkuName(data.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(data.getPrice());
        cartInfo.setCartPrice(data.getPrice());


        return cartInfo;
    }

    /**
     * 根据用户登录信息决定用哪个购物车键
     */
    @Override
    public String determinCartKey() {
        //拿到用户id
        UserAuthInfo info = AuthUtils.getCurrentAuthInfo();
        String cartKey = SysRedisConst.CART_KEY;
        if (info.getUserId() != null) {
            //用户登录了
            cartKey = cartKey + info.getUserId();
        } else {
            //用户未登录用临时id
            cartKey = cartKey + "" + info.getUserTempId();
        }
        return cartKey;
    }
}
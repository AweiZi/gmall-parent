package com.atguigu.starter.cache.aspect;

import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;


@Aspect
@Component
public class CacheAspect {
    /**
     * 目标方法  public SkuDetailTo getSkuDetailWithCache(Long skuId)
     * 连接点：所有的目标方法都在连接点里
     * try{
     * //前置通知
     * 目标方法
     * //返回通知
     * }catch{
     * //异常通知
     * }finally{
     * //后置通知
     * }
     */
    @Autowired
    CacheOpsService cacheOpsService;
    //    创建一个表达式解析器  线程安全
    ExpressionParser parser = new SpelExpressionParser();
    ParserContext context = new TemplateParserContext();

    @Around("@annotation(com.atguigu.starter.cache.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //        Object arg = joinPoint.getArgs()[0];
        Object result = null;
        // key不同方法可能不一样
        String cacheKey = determinCacheKey(joinPoint);
        //1.先查缓存  传目标方法的返回值类型
        Type returnType = getMethodGennericReturnType(joinPoint);
        //SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);

        Object cacheData = cacheOpsService.getCacheData(cacheKey, returnType);

        //2.缓存判断
        if (cacheData == null) {
            //3.准备回源
            //4.先问布隆 有些场景并不一定需要布隆 比如 三级分类（只有一个大数据）
            //            boolean contains = cacheOpsService.bloomContains(arg);
            String bloomName = determinBloomName(joinPoint);
            if (!StringUtils.isEmpty(bloomName)) {
                //指定开启了布隆
                Object bVal = determinBloomValue(joinPoint);
                boolean contains = cacheOpsService.bloomContains(bloomName, bVal);

                if (!contains) {
                    //没有
                    return null;
                }

            }
            //5.布龙说有，准备回源，有击穿风险
            boolean lock = false;
            String lockName = null;
            try {
                //不同场景用自己的锁
                lockName = determinLockName(joinPoint);
                lock = cacheOpsService.tryLock(lockName);
                if (lock) {
                    //6.获取到锁，开始回源,调用目标方法
                    result = joinPoint.proceed(joinPoint.getArgs());
                   long ttl=determinTtl(joinPoint);
                    //7.调用成功,重新保存到缓存
                    cacheOpsService.saveData(cacheKey, result,ttl);

                    return result;

                } else {
                    Thread.sleep(1000l);
                    return cacheData = cacheOpsService.getCacheData(cacheKey, returnType);
                }
            } finally {
                //8.解锁
                if (lock) cacheOpsService.unLock(lockName);
            }
        }


        //缓存中有直接返回
        return cacheData;
    }

    private long determinTtl(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        long ttl = cacheAnnotation.ttl();
        return ttl;
    }

    /**
     * 根据表达式计算出要用的锁的名字
     */
    private String determinLockName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        /**
         * 拿到锁的表达式
         */
        String lockName = cacheAnnotation.lockName();//lock-方法名
        if (StringUtils.isEmpty(lockName)) {
            //没指定锁，用方法级别锁
            return SysRedisConst.LOCK_PREFIX + method.getName();

        }
        String lockNameVal = evaluationExpression(lockName, joinPoint, String.class);
        return lockNameVal;
    }

    /**
     * 根据布隆过滤器之表达式计算出需要判定的值
     */
    private Object determinBloomValue(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        //拿到布隆值表达式
        String bloomValue = cacheAnnotation.bloomValue();
        //布隆算出来的值
        Object expression = evaluationExpression(bloomValue, joinPoint, Object.class);
        return expression;
    }

    /**
     * 获取布隆过滤器的名字
     */
    private String determinBloomName(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        String bloomName = cacheAnnotation.bloomName();

        return bloomName;
    }

    /**
     * 获取目标方法的精确返回值类型
     */
    private Type getMethodGennericReturnType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Type type = method.getGenericReturnType();
        return type;
    }

    /**
     * 根据当前连接点的执行信息，确定缓存用的什么key
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        String expression = cacheAnnotation.cacheKey();
        //计算表达式 根据表达式计算缓存键
        String cacheKey = evaluationExpression(expression, joinPoint, String.class);

        return cacheKey;
    }

    private <T> T evaluationExpression(String expression,
                                       ProceedingJoinPoint joinPoint,
                                       Class<T> clz) {
        //1.创建一个表达式解析器
        //2.得到一个表达式
        Expression exp = parser.parseExpression(expression, context);
        //3.#{#params[0]}
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        //4.去除所有参数，绑定到上下文
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params", args);
        //得到表达式的值
        T expValue = exp.getValue(evaluationContext, clz);
        return expValue;

    }
   /* @Around("@annotation(com.atguigu.gmall.item.cache.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint){
        //1.获取签名将要执行目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2.获取当时目标调用者的所有参数
        Object[] args = joinPoint.getArgs();

        System.out.println(joinPoint.getThis());
        System.out.println(joinPoint.getTarget());

        //3.放行目标方法
        Method method = signature.getMethod();
        //Object obj, Object... args
       //前置通知
        Object result =null;
        try {
            //目标方法执行，并返回返回值
            result = method.invoke(joinPoint.getTarget(), args);
            //返回通知
        } catch (Exception e) {
            //异常通知
            throw new RuntimeException(e);
        }finally {
            //后置通知
        }
        return result;
    }*/
}

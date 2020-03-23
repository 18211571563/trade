package com.trade.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by georgy on 2018/11/26.
 */
//@Aspect
//@Component
public class CommonAspect {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 工行本地服务日志切面
     * @param joinPoint
     * @return
     */
    @Around(value="execution(public * com.tcl.multimedia.nretail.central.polymeric.sales2b.web.controller.OrderController.*(..))")
    public Object aroundMethod(ProceedingJoinPoint joinPoint){
//            log.info("支付网关调用ICBC本地服务({}.{})入参数据：{}", joinPoint.getSignature().getDeclaringType().getSimpleName(),  joinPoint.getSignature().getName(),(joinPoint.getArgs() != null ? JSON.toJSONString(joinPoint.getArgs()): "null") );
//            log.info("支付网关调用ICBC本地服务({}.{})返回数据：{}", joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), (result != null ? JSON.toJSONString(result): "null") );
//        logger.info(JSON.toJSONString(joinPoint.getArgs()));

        Object result = null;
        try {
            result = joinPoint.proceed();
        }catch (Throwable  e){
            logger.error("{}.{} : ",joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), e);
            e.printStackTrace();
        }
        return result;
    }

}

package org.test.awsdemo.common.aop;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.test.awsdemo.common.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;


@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Autowired
    private Environment environment;

    private static final String START_TIME = "request-start";

    /**
     * 切入点
     */
    @Pointcut("execution(* org.test.awsdemo.controller..*.*(..))")
    public void executeResource() {

    }

    /**
     * 前置操作
     *
     * @param point 切入点
     */
    @Before("executeResource()")
    public void beforeLog(JoinPoint point) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        log.debug("【URL】：{}", request.getRequestURL());
        log.debug("【IP】：{}", request.getRemoteAddr());
        log.debug("【Class】：{}，【Method】：{}", point.getSignature().getDeclaringTypeName(), point.getSignature().getName());
        log.debug("【Payload】：{}，", JSONUtil.obj2json(point.getArgs()));
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.debug("【Parameters】：{}，", JSONUtil.obj2json(parameterMap));
        Long start = System.currentTimeMillis();
        request.setAttribute(START_TIME, start);
    }

    /**
     * 环绕操作
     *
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("executeResource()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        log.debug("【Response】：{}", JSONUtil.obj2json(result));
        return result;
    }

    /**
     * 后置操作
     */
    @AfterReturning("executeResource()")
    public void afterReturning() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        String userAgent = request.getHeader("User-Agent");
        Long start = (Long) request.getAttribute(START_TIME);
        Long end = System.currentTimeMillis();
        log.debug("【Time】：{}ms", end - start);
        log.debug("【Environment】：{}", environment.getActiveProfiles()[0]);
        log.debug("【User-Agent】：{}", userAgent);
    }
}

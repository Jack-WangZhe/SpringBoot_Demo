package com.study.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Aspect//标记类为Aspect类
@Component//引入到Spring容器中
public class HttpAspect {

    //org.slf4j.Logger是Spring自带的日志记录框架
    private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    @Pointcut("execution(public * com.study.demo.controller.HelloController.girlList(..))")//.girlList(..)表示girlList方法不管是任何参数都会被拦截;.*(..)则表示该类的所有方法都会被拦截
    public void log(){

    }

    //定义在http请求到方法之前将内容记录下来
    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //url
        logger.info("url={}",request.getRequestURL());
        //method
        logger.info("method={}",request.getMethod());
        //客户端的ip
        logger.info("ip={}",request.getRemoteAddr());
        //请求的类方法,通过当前类的参数传入的JoinPoint对象获取对应的内容
        //joinPoint.getSignature().getDeclaringTypeName()获取的是类名
        //joinPoint.getSignature().getName()获取类方法名
        logger.info("class_method={}",joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
        //参数
        logger.info("args={}",joinPoint.getArgs());
    }

    //定义在执行完方法后调用
    @After("log()")
    public void doAfter(){
        logger.info("执行方法之后的调用...");
    }

    @AfterReturning(returning = "object",pointcut = "log()")
    public void doAfterReturning(Object object){
        logger.info("response={}",object);
    }
}
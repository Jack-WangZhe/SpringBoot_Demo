package com.study.demo.handle;

import com.study.demo.domain.Result;
import com.study.demo.exception.GirlException;
import com.study.demo.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandle {

    public static final Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    //声明要捕获哪个异常类
    @ExceptionHandler(value = Exception.class)
    //由于要返回的是一个json，但是类上方又没有@RestController注解，故需要用ResponseBody修饰
    @ResponseBody
    public Result handle(Exception e){
        if(e instanceof GirlException){
            GirlException girlException = (GirlException)e;
            return ResultUtil.error(girlException.getCode(),girlException.getMessage());
        }else{
            logger.error("[系统异常]{}",e);
            return ResultUtil.error(-1,"未知错误");
        }
    }
}

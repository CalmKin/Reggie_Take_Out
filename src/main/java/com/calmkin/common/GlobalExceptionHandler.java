package com.calmkin.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice   //这个注解就是为rest风格开发的controller类做增强
public class GlobalExceptionHandler {

    @ExceptionHandler
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex)  //捕获所有完整性约束相关的异常
    {
        if(ex.getMessage().contains("Duplicate entry")) //如果是存在重复
        {
            //获取重复的信息
            //Duplicate entry 'zhangsan' for key 'employee.idx_username'
            String[] msgs = ex.getMessage().split(" "); //用空格隔开错误信息
            String dupil = msgs[2];     //获取重复的值
            return R.error(dupil+"已存在");        //因为RestControllerAdvice注解里面包含ResponseBody这个注解，所以这里返回的信息会在前端弹窗的页面里面显示出来
        }

        return R.error("未知错误");
    }


}

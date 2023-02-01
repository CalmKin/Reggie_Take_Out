package com.calmkin.common;

public class CustomException extends RuntimeException{
    public CustomException(String message)
    {
        super(message);     //调用父类RuntimeException的构造器，产生一个错误信息为message的异常
    }
}

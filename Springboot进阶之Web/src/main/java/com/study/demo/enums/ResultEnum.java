package com.study.demo.enums;

//使用枚举将错误编号与错误信息统一管理
public enum ResultEnum {
    UNKOWN_ERROR(-1,"未知错误"),
    SUCCESS(0,"成功"),
    PRIMARY_SCHOOL(100,"你可能在上小学~"),
    MIDDLE_SCHOOL(101,"你可能在上初中~")
    ;

    private Integer code;

    private String msg;

    //枚举的使用基本上不用set方法，而使用构造方法
    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

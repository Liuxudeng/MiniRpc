package com.mini.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

//自动加上所有属性的get set toString() hashcode equals方法
@Data

//添加一个含有所有已声明字段数学参数的构造函数

@AllArgsConstructor




public class HelloObject implements Serializable {

    private Integer id;
    private String message;
}

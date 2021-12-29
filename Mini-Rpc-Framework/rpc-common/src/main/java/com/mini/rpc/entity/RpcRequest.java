package com.mini.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 这个类里的各种变量是在客户端和服务端之间进行通行的时候需要传递的必要的信息
 */


@Data

//使用创建者模式 一次性给所有变量附初始值
@Builder
public class RpcRequest implements Serializable {

    /**
     * 待调用接口的名称
     */

    private String interfaceName;

    /**
     * 待调用方法名称
     */

    private String methodName;


    /**
     * 待调用方法的参数
     */
    private Object[] parameters;

    /**
     * 待调用方法的参数类型
     */

    private Class<?>[] paramTypes;



}

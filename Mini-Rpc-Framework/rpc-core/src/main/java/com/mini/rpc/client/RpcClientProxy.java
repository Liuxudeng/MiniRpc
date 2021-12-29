package com.mini.rpc.client;

import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {





    //服务端地址
    private String host;
    private int port;

    //传递host和port来确定服务端的位置
    public RpcClientProxy(String host,int port){
        this.host = host;
        this.port = port;
    }

    //抑制编译器产生警告信息
@SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        //创建代理对象

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }





    /**
     * 实现invoke方法 指明代理对象的方法被调用时的动作
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder().interfaceName(method.getDeclaringClass()
                        .getName()).methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();


        //进行远程调用的客户端
        RpcClient rpcClient = new RpcClient();

       return ((RpcResponse)rpcClient.sendRequest(rpcRequest,host,port)).getData();
      //  return ((RpcResponse)rpcClient.sendRequest(rpcRequest, host, port)).getData();
    }
}

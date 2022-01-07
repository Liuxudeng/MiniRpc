package com.mini.rpc;


import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcClientProxy implements InvocationHandler {


    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);


    private final com.mini.rpc.RpcClient client;

    //传递host和port来确定服务端的位置
    public RpcClientProxy(com.mini.rpc.RpcClient client){
       this.client = client;
    }

    //抑制编译器产生警告信息
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        //创建代理对象
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
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
    public Object invoke(Object proxy, Method method, Object[] args) {
        /**
         * 注意：这里的interfaceName methodName parameters paramTypes都是客户端要发送出去的信息
         *
         * 我们将它组成rpcRequest对象
         */
//        RpcRequest rpcRequest = RpcRequest.builder().interfaceName(method.getDeclaringClass()
//                        .getName()).methodName(method.getName())
//                .parameters(args)
//                .paramTypes(method.getParameterTypes())
//                .build();
//
//
//        //进行远程调用的客户端
//        com.mini.rpc.client.RpcClient rpcClient = new RpcClient();
//
//       return ((RpcResponse)rpcClient.sendRequest(rpcRequest,host,port)).getData();
      //  return ((RpcResponse)rpcClient.sendRequest(rpcRequest, host, port)).getData();

    logger.info("调用方法：{}#{}", method.getDeclaringClass().getName(), method.getName());
 //   RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),

        //注意这里RpcRequest参数的参数的数据要和原始接口中的数据保持一致
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
            method.getName(), args, method.getParameterTypes(),UUID.randomUUID().toString());
    return client.sendRequest(rpcRequest);

}
}

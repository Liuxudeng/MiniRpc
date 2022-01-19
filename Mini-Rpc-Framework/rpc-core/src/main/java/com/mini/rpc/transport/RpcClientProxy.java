package com.mini.rpc.transport;


import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.transport.netty.client.NettyClient;

import com.mini.rpc.transport.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RpcClientProxy implements InvocationHandler {


    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);


    private final RpcClient client;

    //传递host和port来确定服务端的位置
    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    //抑制编译器产生警告信息
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        //创建代理对象
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }


    /**
     * 实现invoke方法 指明代理对象的方法被调用时的动作
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */

    @SuppressWarnings("unchecked")
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
                method.getName(), args, method.getParameterTypes(), UUID.randomUUID().toString(), true);
        //  return client.sendRequest(rpcRequest);

        Object result = null;
        if (client instanceof NettyClient) {
            //异步获取调用结果
            CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>)
                    client.sendRequest(rpcRequest);

            try {
                result = completableFuture.get().getData();

            } catch (InterruptedException | ExecutionException e) {
                logger.error("方法调用请求发动失败", e);
                return null;
            }
        }

            if (client instanceof SocketClient) {
                RpcResponse rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
                result = rpcResponse.getData();
            }



        return result;

    }
}

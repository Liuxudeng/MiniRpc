package com.mini.test;

import com.mini.rpc.serializer.HessianSerializer;
import com.mini.rpc.transport.RpcClient;
import com.mini.rpc.transport.RpcClientProxy;
import com.mini.rpc.api.HelloObject;
import com.mini.rpc.api.HelloService;
import com.mini.rpc.transport.netty.client.NettyClient;
import com.mini.rpc.serializer.ProtostuffSerializer;

/**
 * 测试用Netty客户端
 */
public class NettyTestClient {
    public static void main(String[] args) {
      //  NettyClient client = new NettyClient("127.0.0.1", 9919);
        RpcClient client = new NettyClient();
        client.setSerializer(new ProtostuffSerializer());


        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);


        HelloObject object = new HelloObject(13,"this is hessian_serialize style");
//
//        System.out.println("------------");
//        System.out.println(object.getMessage());

        String res = helloService.hello(object);

        System.out.println(res);

    }
}

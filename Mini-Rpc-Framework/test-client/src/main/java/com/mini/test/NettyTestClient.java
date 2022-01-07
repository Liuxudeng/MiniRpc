package com.mini.test;

import com.mini.rpc.RpcClientProxy;
import com.mini.rpc.api.HelloObject;
import com.mini.rpc.api.HelloService;
import com.mini.rpc.netty.client.NettyClient;
import com.mini.rpc.serializer.HessianSerializer;
import com.mini.rpc.serializer.KryoSerializer;

/**
 * 测试用Netty客户端
 */
public class NettyTestClient {
    public static void main(String[] args) {
        NettyClient client = new NettyClient("127.0.0.1", 9919);

        client.setSerializer(new KryoSerializer());

        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13,"this is hessian_serialize style");
        String res = helloService.hello(object);
        System.out.println(res);

    }
}

package com.mini.rpc.test;

import com.mini.rpc.api.HelloService;
import com.mini.rpc.serializer.ProtostuffSerializer;
import com.mini.rpc.transport.netty.server.NettyServer;

/**
 * 测试用Netty服务端
 */
public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(new ProtostuffSerializer());
       server.publishService(helloService,HelloService.class);
    }
}

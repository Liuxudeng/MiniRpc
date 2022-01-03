package com.mini.rpc.test;

import com.mini.rpc.netty.server.NettyServer;
import com.mini.rpc.registry.DefaultServiceRegistry;
import com.mini.rpc.registry.ServiceRegistry;

/**
 * 测试用Netty服务端
 */
public class NettyTestServer {

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();

        registry.register(helloService);

        NettyServer server = new NettyServer();
        server.start(9998);
    }
}

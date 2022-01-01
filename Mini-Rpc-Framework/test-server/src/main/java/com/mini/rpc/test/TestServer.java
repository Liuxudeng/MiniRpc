package com.mini.rpc.test;

import com.mini.rpc.api.HelloService;
import com.mini.rpc.registry.DefaultServiceRegistry;
import com.mini.rpc.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        //创建服务容器
        DefaultServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        //注册HelloServiceImpl服务
        serviceRegistry.register(helloService);




        RpcServer rpcServer = new RpcServer(serviceRegistry);

        rpcServer.start(9000);




    }
}

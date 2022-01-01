package com.mini.rpc.test;

import com.mini.rpc.api.HelloService;
import com.mini.rpc.registry.DefaultServiceRegistry;

import com.mini.rpc.socket.server.SocketServer;

public class SocketTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        //创建服务容器
        DefaultServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        //注册HelloServiceImpl服务
        serviceRegistry.register(helloService);




        SocketServer socketServer = new SocketServer(serviceRegistry);

        socketServer.start(9000);




    }
}

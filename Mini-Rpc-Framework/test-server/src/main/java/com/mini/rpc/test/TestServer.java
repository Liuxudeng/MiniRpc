package com.mini.rpc.test;

import com.mini.rpc.api.HelloService;
import com.mini.rpc.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        //注册HelloServiceImpl服务
        rpcServer.register(helloService, 9000);
    }
}

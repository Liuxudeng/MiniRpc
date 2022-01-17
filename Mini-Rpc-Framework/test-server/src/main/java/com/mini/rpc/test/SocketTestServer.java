package com.mini.rpc.test;

import com.mini.rpc.api.HelloService;
import com.mini.rpc.provider.ServiceProviderImpl;

import com.mini.rpc.serializer.CommonSerializer;
import com.mini.rpc.serializer.HessianSerializer;
import com.mini.rpc.transport.socket.server.SocketServer;

public class SocketTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl2();

        SocketServer socketServer = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);

        socketServer.publishService(helloService, HelloService.class);




    }
}

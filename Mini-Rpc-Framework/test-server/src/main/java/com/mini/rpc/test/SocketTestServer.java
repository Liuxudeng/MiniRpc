package com.mini.rpc.test;

import com.mini.rpc.annotation.ServiceScan;
import com.mini.rpc.api.HelloService;
import com.mini.rpc.provider.ServiceProviderImpl;

import com.mini.rpc.serializer.CommonSerializer;
import com.mini.rpc.serializer.HessianSerializer;
import com.mini.rpc.transport.RpcServer;
import com.mini.rpc.transport.socket.server.SocketServer;

/**
 * 测试socket服务端
 */
@ServiceScan
public class SocketTestServer {
    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1",9998,CommonSerializer.KRYO_SERIALIZER);
        server.start();




    }
}

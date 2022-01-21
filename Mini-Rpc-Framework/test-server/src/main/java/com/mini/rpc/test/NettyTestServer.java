package com.mini.rpc.test;

import com.mini.rpc.annotation.ServiceScan;
import com.mini.rpc.api.HelloService;
import com.mini.rpc.serializer.CommonSerializer;
import com.mini.rpc.serializer.HessianSerializer;
import com.mini.rpc.serializer.ProtostuffSerializer;
import com.mini.rpc.transport.RpcServer;
import com.mini.rpc.transport.netty.server.NettyServer;

/**
 * 测试用Netty服务端
 */

@ServiceScan
public class NettyTestServer {

    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1",9999,CommonSerializer.PROTOBUF_SERIALIZER);
    server.start();
    }
}

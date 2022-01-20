package com.mini.test;

import com.mini.rpc.loadbalancer.RoundRobinLoadBalancer;
import com.mini.rpc.serializer.CommonSerializer;
import com.mini.rpc.transport.RpcClient;
import com.mini.rpc.transport.RpcClientProxy;
import com.mini.rpc.api.HelloObject;
import com.mini.rpc.api.HelloService;

import com.mini.rpc.serializer.HessianSerializer;
import com.mini.rpc.transport.socket.client.SocketClient;

public class SocketTestClient {

    public static void main(String[] args) {
        //拿到rerquest
//        SocketClient client = new SocketClient("127.0.0.1",9999);
//        SocketClient client = new SocketClient();
//        client.setSerializer(new HessianSerializer());
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER,new RoundRobinLoadBalancer());
        //接口与代理对象之间的中介对象
        RpcClientProxy proxy = new RpcClientProxy(client);
        //创建代理对象
        HelloService helloService = proxy.getProxy(HelloService.class);
        //接口方法的参数对象
        HelloObject object = new HelloObject(12, "This is test message");
      //返回所有服务
        for (int i = 0; i < 20; i++) {
            //有动态代理可知，代理对象和调用hello()实际汇之星invoke()
            String res = helloService.hello(object);
            System.out.println(res);
        }
    }
}

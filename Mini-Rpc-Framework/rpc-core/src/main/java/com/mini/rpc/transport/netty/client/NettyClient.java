package com.mini.rpc.transport.netty.client;

import com.mini.rpc.registry.NacosServiceDiscovery;
import com.mini.rpc.registry.ServiceDiscovery;
import com.mini.rpc.transport.RpcClient;
import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import com.mini.rpc.registry.NacosServiceRegistry;
import com.mini.rpc.registry.ServiceRegistry;
import com.mini.rpc.serializer.CommonSerializer;
import com.mini.rpc.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {
    //打印日志
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

 private final ServiceDiscovery serviceDiscovery;

    private CommonSerializer serializer;
    //创建server端的两个组

    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true);
    }


  //  private static final Bootstrap bootstrap;

   public NettyClient(){
       //以默认序列化器调用构造函数
       this(DEFAULT_SERIALIZER);
   }

    public NettyClient(Integer serializerCode){
        serviceDiscovery = new NacosServiceDiscovery();
        serializer = CommonSerializer.getByCode(serializerCode);
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

//        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
//            @Override
//            protected void initChannel(SocketChannel ch) throws Exception {
//                ChannelPipeline pipeline = ch.pipeline();
//                pipeline.addLast(new CommonDecoder())
//                        .addLast(new CommonEncoder(serializer))
//                        .addLast(new NettyClientHandler());
//            }
//        });

        AtomicReference<Object> result = new AtomicReference<>(null);




           try {

               //从Nacos获取提供对应服务的服务端地址
               InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());


               //创建Netty通道连接
               Channel channel = ChannelProvider.get(inetSocketAddress, serializer);

               if(!channel.isActive()){
                   group.shutdownGracefully();
                   return null;
               }

               //向服务端发秦秋 并设置监听
               channel.writeAndFlush(rpcRequest).addListener(future -> {
                   if(future.isSuccess()){
                       logger.info(String.format("客户端发送消息：%s",rpcRequest.toString()));
                   }else {
                       logger.error("发送消息时有错误发生：",future.cause());
                   }
               });


               channel.closeFuture().sync();


               //AttributeMap<AttributeKey, AttributeValue>是绑定在Channel上的，可以设置用来获取通道对象
               AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
               //get()阻塞获取value
               RpcResponse rpcResponse = channel.attr(key).get();
               RpcMessageChecker.check(rpcRequest, rpcResponse);
               result.set(rpcResponse.getData());
           }catch (InterruptedException e){
               logger.error("发送消息时有错误发生:", e);
               //interrupt()这里作用是给受阻塞的当前线程发出一个中断信号，让当前线程退出阻塞状态，好继续执行然后结束
               Thread.currentThread().interrupt();
           }



           return result.get();


    }

//    @Override
//    public void setSerializer(CommonSerializer serializer) {
//        this.serializer = serializer;
//
//    }
}

package com.mini.rpc.transport.netty.client;


import com.mini.rpc.factory.SingletonFactory;
import com.mini.rpc.loadbalancer.LoadBalancer;
import com.mini.rpc.loadbalancer.RandomLoadBalancer;
import com.mini.rpc.registry.NacosServiceDiscovery;
import com.mini.rpc.registry.ServiceDiscovery;
import com.mini.rpc.transport.RpcClient;
import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;

import com.mini.rpc.serializer.CommonSerializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;


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
                .channel(NioSocketChannel.class);
    }


  //  private static final Bootstrap bootstrap;
   // private final UnprocessedRequests unprocessedRequests;
    private final UnprocessedRequests unprocessedRequests;
   public NettyClient(){
       //以默认序列化器调用构造函数
       this(DEFAULT_SERIALIZER,new RandomLoadBalancer());
   }

    public NettyClient(LoadBalancer loadBalancer){
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializerCode){

        this(serializerCode, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializerCode,LoadBalancer loadBalancer){
        serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        serializer = CommonSerializer.getByCode(serializerCode);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }


    @Override
   // public Object sendRequest(RpcRequest rpcRequest) {
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest){
        if(serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }


        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

           try {

               //从Nacos获取提供对应服务的服务端地址
               InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());


               //创建Netty通道连接
               Channel channel = ChannelProvider.get(inetSocketAddress, serializer);

               if(!channel.isActive()){
                   group.shutdownGracefully();
                   return null;
               }
                //将新请求放入未处理完的请求中
               unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
               //向服务端发秦秋 并设置监听
            //   channel.writeAndFlush(rpcRequest).addListener(future -> {
               channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                   if(future1.isSuccess()){
                       logger.info(String.format("客户端发送消息：%s",rpcRequest.toString()));
                   }else {
                       future1.channel().close();
                       resultFuture.completeExceptionally(future1.cause());
                       logger.error("发送消息时有错误发生: ", future1.cause());
                   }
               });



           }catch (Exception e){
               //将请求从请求集合中移除
               unprocessedRequests.remove(rpcRequest.getRequestId());
               logger.error(e.getMessage(), e);


               //interrupt()这里作用是给受阻塞的当前线程发出一个中断信号，让当前线程退出阻塞状态，好继续执行然后结束
               Thread.currentThread().interrupt();
           }



           return resultFuture;


    }

//    @Override
//    public void setSerializer(CommonSerializer serializer) {
//        this.serializer = serializer;
//
//    }
}

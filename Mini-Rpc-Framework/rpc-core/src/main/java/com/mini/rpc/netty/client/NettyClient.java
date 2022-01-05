package com.mini.rpc.netty.client;

import com.fasterxml.jackson.databind.JsonSerializable;
import com.mini.rpc.RpcClient;
import com.mini.rpc.codec.CommonDecoder;
import com.mini.rpc.codec.CommonEncoder;
import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.serializer.HessianSerializer;
import com.mini.rpc.serializer.JsonSerializer;
import com.mini.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient implements RpcClient {
    //打印日志
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    //地址以及端口号

    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        //将线程池初始化到启动器中
        bootstrap.group(group)
                //设置服务端通道类型
                .channel(NioSocketChannel.class)
                //启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，默认的心跳间隔是7200s即2小时。
                .option(ChannelOption.SO_KEEPALIVE,true)
                //初始化handler 设置Handler操作
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                /**
                                 * json序列化
                                 */
                            //    .addLast(new CommonEncoder(new JsonSerializer()))

                                /**
                                 * kryo序列化
                                 */
                              //  .addLast(new CommonEncoder(new KryoSerializer()))
                                /**
                                 * hessian序列化
                                 */
                                .addLast(new CommonEncoder(new HessianSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });


    }




    @Override
    public Object sendRequest(RpcRequest rpcRequest) {

           try {
               ChannelFuture future = bootstrap.connect(host,port).sync();
               logger.info("客户端连接到服务端{}:{}",host,port);


               Channel channel = future.channel();
               if(channel!=null){
                   //向服务端发请求 并设置监听
                   // 关于writeAndFlush()的具体实现可以参考：https://blog.csdn.net/qq_34436819/article/details/103937188

                   channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                      if(future1.isSuccess()){
                          logger.info(String.format("客户端发送消息：%s",rpcRequest.toString()));

                      } else{
                          logger.error("发送消息时有错误发生：",future1.cause());
                      }
                   });

                   channel.closeFuture().sync();
                    //AttributeMap<Attribute,AttributeValue>是绑定在Channel上的 可以设置用来获取通道对象
                   AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                   //get()阻塞获取value
                   RpcResponse rpcResponse = channel.attr(key).get();
                   return rpcResponse.getData();

               }


           } catch (InterruptedException e) {
               logger.error("发送消息时有错误发生",e);
           }


           return null;


    }
}

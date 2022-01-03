package com.mini.rpc.netty.server;

import com.mini.rpc.RpcServer;
import com.mini.rpc.codec.CommonDecoder;
import com.mini.rpc.codec.CommonEncoder;
import com.mini.rpc.serializer.JsonSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer implements RpcServer {
    /**
     * 打印日志
     */
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Override
    public void start(int port) {
        /**
         * 首先明确 服务端有两个group 其中bossgroup主要是对客户端的新连接请求进行处理（即OP_ACCEPT事件）
         * WorkGroup中，则负责处理IO读写、编解码、业务逻辑等（即OP_READ事件、OP_WRITE事件）。
         */

        //用户处理客户端新连接的主"线程池"
        EventLoopGroup bossgroup = new NioEventLoopGroup();

        //用于连接后处理IO时间的从"线程池"
        EventLoopGroup workergroup = new NioEventLoopGroup();


        try{
            //初始化Netty服务端启动器 作为服务端入口
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            //将主从线程初始化到启动器中
            serverBootstrap.group(bossgroup,workergroup)
                    //设置服务端通道类型
                    .channel(NioServerSocketChannel.class)
                    //日志打印方式
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //配置ServiceChannel参数 服务端接收连接的最大队列长度 如果队列已满 客户端将被拒绝连接
                    .option(ChannelOption.SO_BACKLOG,256)
                    //启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制 默认心跳间隔是7200s
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    //配置Channel参数 nodelay没有延迟 true就代表禁用Nagle算法 减小传输延迟
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    //初始化Handler 设置Handle操作
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                        //初始化管道
                            ChannelPipeline pipeline = ch.pipeline();
                            //往管道中添加Handler，注意入站Handler与出站Handler都必须按实际执行顺序添加，比如先解码再Server处理，那Decoder()就要放在前面。
                            //但入站和出站Handler之间则互不影响，这里我就是先添加的出站Handler再添加的入站
                            pipeline.addLast(new CommonEncoder(new JsonSerializer()))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());


                        }
                    });

            //绑定端口 启动Netty sync()代表阻塞主线程Server线程
            //以执行Netty线程，如果不阻塞Netty就直接被下面shutdown了

            ChannelFuture future = serverBootstrap.bind(port).sync();
            //等确定通道关闭了，关闭future回到主Server线程
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            logger.error("启动服务器时错误发生",e);
        }finally {
            //优雅关闭Netty服务端且清理掉内存
            bossgroup.shutdownGracefully();
            workergroup.shutdownGracefully();
        }

    }
}

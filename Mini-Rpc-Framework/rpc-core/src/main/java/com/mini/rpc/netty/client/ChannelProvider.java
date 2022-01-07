package com.mini.rpc.netty.client;

import com.mini.rpc.codec.CommonDecoder;
import com.mini.rpc.codec.CommonEncoder;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import com.mini.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * 用于获取channel对象
 */
public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static EventLoopGroup eventLoopGroup;

    private static Bootstrap bootstrap = initializeBootstrap();

    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;


    private static Bootstrap initializeBootstrap(){
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)

                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true);

        return bootstrap;
    }

    public static Channel get (InetSocketAddress intSocketAddress, CommonSerializer serializer){
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });

        //计量计数器值为1
        CountDownLatch countDownLatch = new CountDownLatch(1);


        try {
            connect(bootstrap,intSocketAddress,countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
          logger.error("获取Channel时有错误发生",e);
        }

        return channel;

    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress,CountDownLatch countDownLatch){
        connect(bootstrap,inetSocketAddress,MAX_RETRY_COUNT,countDownLatch);
    }

    /**
     * Netty客户端创建通道连接 实现连接失败机制
     */

    private static void connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress
            ,int retry,CountDownLatch countDownLatch){
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future->{
            if(future.isSuccess()){
                logger.info("客户端连接成功");
                channel = future.channel();

                //计数器减1

                countDownLatch.countDown();

                return;
            }

            if(retry==0){
                logger.error("客户端连接失败：重试次数已用完，放弃连接");
                countDownLatch.countDown();

                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);

            }

            //第几次重连
            int order = (MAX_RETRY_COUNT - retry) + 1;
            //重连的时间间隔，相当于1乘以2的order次方
            int delay = 1 << order;
            logger.error("{}:连接失败，第{}次重连……", new Date(), order);
            //利用schedule()在给定的延迟时间后执行connect()重连
            //注意这里的代码和参考示例不同 这里不用.config()方法 因为本项目采用的是netty5依赖
            bootstrap.group().schedule(() ->
                            connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch)
                    , delay, TimeUnit.SECONDS);
        });


    }


}

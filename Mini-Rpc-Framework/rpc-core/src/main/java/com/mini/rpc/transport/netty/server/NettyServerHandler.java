package com.mini.rpc.transport.netty.server;


import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.handler.RequestHandler;
import com.mini.rpc.util.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Netty中处理从客户端传来的RpcRequest
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private static RequestHandler requestHandler;


    private static final String THREAD_NAME_PREFIX = "netty-server-handler";

    private static final ExecutorService theadPool;

static {
    requestHandler = new RequestHandler();
    //引入异步线程池，避免长时间的耗时业务阻塞netty本身的worker工作线程
    //耽误了同一个selector中其他任务的执行

    theadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
}


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {

      theadPool.execute(()->{
          try{
              logger.info("服务端接收到的请求：{}",msg);

              Object response = requestHandler.handle(msg);
              //注意这里的通道是workGroup中的，而NettyServer中创建的是bossGroup的，不要混淆
              ChannelFuture future = ctx.writeAndFlush(response);
              //添加一个监听器到channelfuture来检测是否所有的数据包都发出，然后关闭通道
              future.addListener(ChannelFutureListener.CLOSE);
          }finally {
              ReferenceCountUtil.release(msg);
          }
      });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }
}

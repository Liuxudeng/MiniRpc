package com.mini.rpc.netty.server;

import com.mini.rpc.RequestHandler;
import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.registry.DefaultServiceRegistry;
import com.mini.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty中处理从客户端传过来的RpcRequest
 */

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static{
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
//
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * @description: 注意这里和csdn上的教程不同 这里重写的方法是messageReceived方法 而csdn上重写的channelRead0方法
     * 这是因为本例中的netty依赖为5.0以上版本导致的,是5.0版本以后的。
     * SimpleChannelInboundHandler 实现了一个messageReceived方法，这之前的版本并没有。
     * 不过不用担心，比较了相关代码，其实底层逻辑都是一样的。只不过名字具体化了，
     * 毕竟messageReceived 比 channelRead0 方法易懂啊！！！！！
     *
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try{
            logger.info("服务端接收到请求：{}", msg);
            String interfaceName = msg.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
        Object response = requestHandler.handle(msg,service);
        ChannelFuture future = ctx.writeAndFlush(response);
          // ChannelFuture future = ctx.writeAndFlush(result);
            //添加一个监听器到channelfuture来检测是否所有的数据包都发出，然后关闭通道
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
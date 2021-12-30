package com.mini.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 服务端实现反射调用
 */
public class RpcServer {
    private final ExecutorService threadPool;
    //设置日志
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);


    public RpcServer() {
       // this.threadPool = threadPool1;
        /**
         * 设置线程池默认相关
         */
        int corePoolSize = 5;
        int maximumPoolSize = 50;

        long keepAliveTime = 60;


        /**
         * 设置上限为100个线程的阻塞队列
         */

        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        /**
         * 创建线程实例
         */

    threadPool =    new ThreadPoolExecutor(corePoolSize,maximumPoolSize,
                keepAliveTime,TimeUnit.SECONDS,workingQueue,threadFactory);
    }


    /**
     * 注册服务
     */

    public void register(Object service, int port){
        logger.info("服务器正在被启动...111");
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器正在被启动...222");
            Socket socket;

            //当未接收到请求时，accept会一直阻塞

            while ((socket = serverSocket.accept())!=null){
              //  System.out.println(socket.getInetAddress().getHostAddress());
                logger.info("客户端连接！IP："+socket.getInetAddress().getHostAddress());
                threadPool.execute(new WorkerThread(socket,service));
            }

        }catch (IOException e){
            logger.info("连接时有错误发生：" + e);
        }

    }
}

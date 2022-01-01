package com.mini.rpc.server;

import com.mini.rpc.registry.ServiceRegistry;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import com.sun.scenario.effect.impl.prism.PrImage;
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

    /**
     * 线程池相关
     */
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXMIUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;


    private RequestHandler requestHandler = new RequestHandler();

    private final ServiceRegistry serviceRegistry;


    public RpcServer(ServiceRegistry serviceRegistry) {
        // this.threadPool = threadPool1;
        /**
         * 设置线程池默认相关
         */
//        int corePoolSize = 5;
//        int maximumPoolSize = 50;
//
//        long keepAliveTime = 60;\

        this.serviceRegistry = serviceRegistry;


        /**
         * 设置上限为100个线程的阻塞队列
         */

        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        /**
         * 创建线程实例
         */

        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXMIUM_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }
    /**
     * 注册服务
     */
        public void start(int port){
            try (ServerSocket serverSocket = new ServerSocket(port)) {
              //  logger.info("服务器正在启动...");
                logger.info("服务器启动...");
                Socket socket;
                //当未接收到请求时， accpet()会一直阻塞

                while ((socket = serverSocket.accept()) != null) {
                    logger.info("客户端连接！{}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
                    threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
                }
                threadPool.shutdown();
            } catch (IOException e) {
                logger.info("服务器启动时有错误发生：" + e);
            }





    }

}

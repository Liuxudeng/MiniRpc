package com.mini.rpc.transport.socket.server;

import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import com.mini.rpc.handler.RequestHandler;

import com.mini.rpc.provider.ServiceProvider;
import com.mini.rpc.provider.ServiceProviderImpl;
import com.mini.rpc.registry.NacosServiceRegistry;
import com.mini.rpc.registry.ServiceRegistry;
import com.mini.rpc.serializer.CommonSerializer;

import com.mini.rpc.transport.RpcServer;
import com.mini.rpc.util.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 服务端实现反射调用
 */
public class SocketServer implements RpcServer {

    //设置日志
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

//    /**
//     * 线程池相关
//     */
//    private static final int CORE_POOL_SIZE = 5;
//    private static final int MAXMIUM_POOL_SIZE = 50;
//    private static final int KEEP_ALIVE_TIME = 60;
//    private static final int BLOCKING_QUEUE_CAPACITY = 100;


    private RequestHandler requestHandler = new RequestHandler();
    private final ExecutorService threadPool;

    private final ServiceRegistry serviceRegistry;

    private CommonSerializer serializer;


    private final String host;
    private final int port;


        private final ServiceProvider serviceProvider;
         public SocketServer(String host, int port){
            this.host = host;
            this.port = port;
            serviceRegistry = new NacosServiceRegistry();
            serviceProvider = new ServiceProviderImpl();
        // this.threadPool = threadPool1;
        /**
         * 设置线程池默认相关
         */
//        int corePoolSize = 5;
//        int maximumPoolSize = 50;
//
//        long keepAliveTime = 60;\




        /**
         * 设置上限为100个线程的阻塞队列
         */

//        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//
//        /**
//         * 创建线程实例
//         */
//
//        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXMIUM_POOL_SIZE,
//                KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);

        //创建线程池
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }
    /**
     * 服务端启动
     */
        @Override
        public void  start(){
            try (ServerSocket serverSocket = new ServerSocket(port)) {
              //  logger.info("服务器正在启动...");
                logger.info("服务器启动...");
                Socket socket;
                //当未接收到请求时， accpet()会一直阻塞

                while ((socket = serverSocket.accept()) != null) {
                    logger.info("客户端连接！{}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
                    threadPool.execute(new RequestHandlerThread(socket, requestHandler, serializer));
                }
                threadPool.shutdown();
            } catch (IOException e) {
                logger.info("服务器启动时有错误发生：" + e);
            }





    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 将服务保存在本地的注册表，同时注册到Nacos注册中心
     * @param service
     * @param serviceClass
     * @param <T>
     */
    @Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        if (serializer == null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service,serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

}

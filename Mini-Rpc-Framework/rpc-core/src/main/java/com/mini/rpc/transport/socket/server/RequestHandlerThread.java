package com.mini.rpc.transport.socket.server;


import com.mini.rpc.handler.RequestHandler;
import com.mini.rpc.registry.ServiceRegistry;

import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.serializer.CommonSerializer;
import com.mini.rpc.transport.socket.util.ObjectReader;
import com.mini.rpc.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class RequestHandlerThread implements Runnable{


    //日志文件
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;

    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler
            ,CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;


        this.serializer = serializer;
    }

    @Override
    public void run() {
        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
           Object response = requestHandler.handle(rpcRequest);
            ObjectWriter.writeObject(outputStream, response, serializer);
        }catch (IOException e){
            logger.info("调用或发送时发生错误：" + e);
        }

    }
}

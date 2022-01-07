package com.mini.rpc.socket.client;


import com.mini.rpc.RpcClient;
import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.enumeration.ResponseCode;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import com.mini.rpc.serializer.CommonSerializer;
import com.mini.rpc.socket.util.ObjectReader;
import com.mini.rpc.socket.util.ObjectWriter;
import com.mini.rpc.util.RpcMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private CommonSerializer serializer;

    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        /**
         * 先判断是否设置了序列化器
         */

        if(serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }


        /**
         * socket套接字实现TCP网络传输
         * try()中一般放对资源的申请，若{}出现异常，()资源会自动关闭
         */
        try (Socket socket = new Socket(host, port)) {

            /**
             * Socket类的getInputStream方法与getOutputStream方法的使用
             * 客户端上的使用
             *
             * getInputStream方法可以得到一个输入流，客户端的Socket对象上的getInputStream方法得到输入流其实就是从服务器端发回的数据。
             * getOutputStream方法得到的是一个输出流，客户端的Socket对象上的getOutputStream方法得到的输出流其实就是发送给服务器端的数据。
             * 服务器端上的使用
             *
             * getInputStream方法得到的是一个输入流，服务端的Socket对象上的getInputStream方法得到的输入流其实就是从客户端发送给服务器端的数据流。
             *
             * getOutputStream方法得到的是一个输出流，服务端的Socket对象上的getOutputStream方法得到的输出流其实就是发送给客户端的数据。
             *
             */

//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            //将要传递的信息写入objectOutputStream对象
//            objectOutputStream.writeObject(rpcRequest);
//            objectOutputStream.flush();
//
//            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ObjectWriter.writeObject(outputStream,rpcRequest,serializer);

            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;


            RpcMessageChecker.check(rpcRequest,rpcResponse);

            return rpcResponse.getData();


            //读到inputStream接收的消息
            // return objectInputStream.readObject();
        } catch (IOException e) {
            logger.error("调用时有错误发生：" + e);
            //   return null;

            throw new RpcException("服务调用失败：", e);
        }

    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

}




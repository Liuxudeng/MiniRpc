//package com.mini.rpc.socket.server;
//
//import com.mini.rpc.entity.RpcRequest;
//import com.mini.rpc.entity.RpcResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.Socket;
//
///**
// * 处理客户端rpcrequest的工作线程
// */
//
//public class WorkerThread implements Runnable{
//
//    //日志文件
//    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);
//
//    //三个变量
//
//    private Socket socket;
//    private Object service;
//
//
//    public WorkerThread(Socket socket,Object service){
//        this.socket = socket;
//
//
//    }
//
//
//
//
//
//
//
//
//
//    @Override
//    public void run() {
//        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
//
//            //利用反射原理找到远程所需调用的方法
//            RpcRequest rpcRequest = (RpcRequest)objectInputStream.readObject();
//            //invoke(obj实例对象,obj可变参数)
//            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
//            Object returnObject = method.invoke(service, rpcRequest.getParameters());
//            objectOutputStream.writeObject(RpcResponse.success(returnObject));
//            objectOutputStream.flush();
//
//        }catch (IOException e){
//            logger.info("调用或发送时有错误发生: "+e);
//
//        } catch (ClassNotFoundException e) {
//            logger.info("调用或发送时有错误发生: "+e);
//        } catch (NoSuchMethodException e) {
//            logger.info("调用或发送时有错误发生: "+e);
//        } catch (InvocationTargetException e) {
//            logger.info("调用或发送时有错误发生: "+e);
//        } catch (IllegalAccessException e) {
//            logger.info("调用或发送时有错误发生: "+e);
//        }
//
//    }
//}

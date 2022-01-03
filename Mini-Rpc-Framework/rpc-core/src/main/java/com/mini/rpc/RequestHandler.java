package com.mini.rpc;

import com.mini.rpc.entity.RpcRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    //打印日志
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

   //handle方法
    public Object handle(RpcRequest rpcRequest,Object service){
        Object result = null;
        try{
            result = invokeTargetMethod(rpcRequest,service);
            logger.info("服务：{}成功调用方法：{}",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());


        }catch (IllegalAccessException| InvocationTargetException e){
            logger.info("调用发生错误"+e);

        }

        return result;

    }


    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
        }catch (NoSuchMethodException e){
         //   return RpcResponse.fail(ResponseCode.CLASS_NOT_FOUND);

            logger.info("调用或发送时有错误发生："+e);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }

}

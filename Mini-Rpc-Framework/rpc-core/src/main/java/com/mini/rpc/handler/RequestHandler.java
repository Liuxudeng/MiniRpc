package com.mini.rpc.handler;

import com.mini.rpc.entity.RpcRequest;

import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.enumeration.ResponseCode;
import com.mini.rpc.provider.ServiceProvider;
import com.mini.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    //打印日志
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;
   //handle方法
    static {
        serviceProvider = new ServiceProviderImpl();
   }

   public Object handle(RpcRequest rpcRequest){
        Object result = null;

        //从服务端本地注册表获取服务实体
       Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        try{
            result = invokeTargetMethod(rpcRequest,service);
            logger.info("服务：{}成功调用方法：{}",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());


        }catch (IllegalAccessException| InvocationTargetException e){
            logger.info("调用发生错误"+e);

        }

        return RpcResponse.success(result,rpcRequest.getRequestId());

    }


    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) throws InvocationTargetException, IllegalAccessException {
        Method method ;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
        }catch (NoSuchMethodException e){
         //   return RpcResponse.fail(ResponseCode.CLASS_NOT_FOUND);

          //  logger.info("调用或发送时有错误发生："+e);
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());
    }

}

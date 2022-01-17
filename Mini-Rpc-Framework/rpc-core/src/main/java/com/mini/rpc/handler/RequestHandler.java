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

   public Object handle(RpcRequest rpcRequest) {


        //从服务端本地注册表获取服务实体
       Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());


        return invokeTargetMethod(rpcRequest,service);

    }


    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) {
      Object result;
        try{
           Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            result = method.invoke(service,rpcRequest.getParameters());

            logger.info("服务：{}成功调用方法：{}",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
          //  logger.info("调用或发送时有错误发生："+e);
          //  return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }catch  (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            //方法调用失败
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
    }
        //方法调用成功
        return RpcResponse.success(result,rpcRequest.getRequestId());
}
}

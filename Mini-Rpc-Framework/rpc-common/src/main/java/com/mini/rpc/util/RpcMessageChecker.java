package com.mini.rpc.util;

import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.enumeration.ResponseCode;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查响应和请求
 */


public class RpcMessageChecker {

    private static final String INTERFACE_NAME = "interfaceName";

    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker(){

    }

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        /**
         * 首先判断response是否为空
         */
        if(rpcResponse==null){
            logger.error("调用服务失败,serviceName:{}",rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE
                    ,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }

        //判断响应与请求的请求号是否相同
        if(!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            //不同的话 抛出响应不匹配的错误
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH
                    ,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }

        //如果出现调用失败

        if(rpcResponse.getStatusCode()==null||!rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())){
            logger.error("调用服务失败，serviceName:{}，RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }






}

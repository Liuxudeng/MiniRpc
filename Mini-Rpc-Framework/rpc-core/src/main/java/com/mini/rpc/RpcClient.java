package com.mini.rpc;

import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 */
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);

    /**
     * 增加一个对socket序列化的接口
     */

    void setSerializer(CommonSerializer serializer);
}

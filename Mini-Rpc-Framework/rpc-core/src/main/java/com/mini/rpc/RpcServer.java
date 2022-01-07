package com.mini.rpc;

import com.mini.rpc.serializer.CommonSerializer;

public interface RpcServer {
    void start(int port);

    /**
     * 同样添加socket序列化的方法
     */

    void setSerializer(CommonSerializer serializer);
}

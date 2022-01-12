package com.mini.rpc.transport;

import com.mini.rpc.serializer.CommonSerializer;

/**
 * 服务端类通过接口
 */
public interface RpcServer {

    void start();

    /**
     * 同样添加socket序列化的方法
     */

    void setSerializer(CommonSerializer serializer);

    /**
     * 向nacos中注册服务
     */

    <T> void publishService(Object service,Class<T> serviceClass);


}
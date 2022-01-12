package com.mini.rpc.registry;

import java.net.InetSocketAddress;

/**
 *
 * 服务注册中心nacos 通用接口
 */
public interface ServiceRegistry {
    /**
     * 将一个服务注册到注册表
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 根据服务名查找服务实体
     */

    InetSocketAddress lookupService(String serviceName);

}

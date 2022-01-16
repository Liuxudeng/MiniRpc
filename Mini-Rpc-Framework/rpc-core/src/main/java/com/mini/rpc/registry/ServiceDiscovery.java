package com.mini.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名称查找服务端地址
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);


}

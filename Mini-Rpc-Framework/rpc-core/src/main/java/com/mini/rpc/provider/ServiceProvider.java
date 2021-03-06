package com.mini.rpc.provider;

/**
 * 保存和提供实例服务
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service,String serviceName);
    Object getServiceProvider(String serviceName);
}

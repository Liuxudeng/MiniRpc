package com.mini.rpc.provider;

/**
 * 保存和提供实例服务
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service,Class<T> serviceClass);
    Object getServiceProvider(String serviceName);
}

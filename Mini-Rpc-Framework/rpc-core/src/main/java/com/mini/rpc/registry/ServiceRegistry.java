package com.mini.rpc.registry;

/**
 *
 * 因为我们设计的rpc不可能只有一个服务 因此需要容器来保存客户端提供的所有服务
 * 服务注册表
 * 用来保存服务端提供的所有服务，方便查询使用，
 * 即通过服务名字就能返回这个服务的具体信息（利用接口名字获取到具体接口实现类对象）。
 * 创建一个 ServiceRegistry 接口
 */
public interface ServiceRegistry {
    /**
     * 将一个服务注册进注册表
     * @param service  待注册的服务实体类
     * @param <T>  服务实体类
     *
     * @return [void]
     * @date
     */


    <T> void register(T service);

    /**
     *根据服务名称获取服务实体
     *
     * @param serviceName 服务名称
     * @return  返回的内容是服务实体
     */

    Object getService(String serviceName);
}

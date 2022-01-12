package com.mini.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.List;

/**
 * nacos服务注册中心的具体配置
 */
public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    //设置nacos的ip和端口号
    private static final String SERVER_ADDR = "192.168.31.123:8848";
    private static final NamingService namingService;


    static {
       //连接nacos创建命名服务
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接Nacos时有错误发生：" + e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * @description 将服务的名称和地址注册进服务注册中心
     * @param
     * @return [void]
     * @date [2021-03-13 15:40]
     */

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {

        try {
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.info("注册服务时有错误发生");
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }


    }

    /**
     * 根据服务名称从注册中心获取到一个服务提供者的地址
     * @param serviceName
     * @return
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(),instance.getPort());

        } catch (NacosException e) {
            logger.error("获取服务时有错误发生"+e);
        }

        return null;
    }
}

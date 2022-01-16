package com.mini.rpc.util;

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
 * 管理Nacos等连接类
 */


public class NacosUtil {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final String SERVER_ADRR = "127.0.0.1:8848";

    /**
     * @description :连接Nacos创建命名空间
     * @return
     */
    public static NamingService getNacosNamingService(){
        try{
            return NamingFactory.createNamingService(SERVER_ADRR);
        }catch (NacosException e){
            logger.error("连接Nacos时发生错误",e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * 注册服务到nacos
     * @param namingService
     * @param serviceName
     * @param inetSocketAddress
     * @throws NacosException
     */
    public static void registerService(NamingService namingService, String serviceName
            , InetSocketAddress inetSocketAddress) throws NacosException {
        namingService.registerInstance(serviceName,inetSocketAddress.getHostName()
                ,inetSocketAddress.getPort());
    }

    /**
     * 获取所有提供该服务的服务端地址
     * @param namingService
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(NamingService namingService
            , String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);

    }



}

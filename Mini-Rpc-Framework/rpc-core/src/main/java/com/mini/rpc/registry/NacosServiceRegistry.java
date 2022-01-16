package com.mini.rpc.registry;



import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import com.mini.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.List;

/**
 * nacos服务注册中心的具体配置
 */
public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

   public final NamingService namingService;

   public NacosServiceRegistry(){
       namingService = NacosUtil.getNacosNamingService();
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
            //Nacos注册服务
                NacosUtil.registerService(namingService,serviceName,inetSocketAddress);
        } catch (NacosException e) {
            logger.info("注册服务时有错误发生");
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }


    }



}
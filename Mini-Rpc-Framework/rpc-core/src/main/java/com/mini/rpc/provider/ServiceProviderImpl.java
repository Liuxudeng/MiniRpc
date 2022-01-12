package com.mini.rpc.provider;


import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import com.mini.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 新建一个默认的注册表类 DefaultServiceRegister来实现这个接口 提供服务注册服务
 */
public class ServiceProviderImpl implements ServiceProvider{

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    /**
     * key=服务名称  value = 服务实体
     */

    private static Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    /**
     * 用来存放实现类的名称 set 存取更加高效 存放实现类名称相比于接口名称所占内存
     * 更小 因为一个实现类可能实现多个接口
     *
     * 这里的registeredService即为注册表
     */
    private static Set<String> registeredService =  ConcurrentHashMap.newKeySet();


    /**
     *
     * @param service  待注册的服务实体类
     * @param <T>
     */


    @Override
    public  <T> void addServiceProvider(T service) {
        /**
         * getName() 和 getCanonicalName() 在获取普通类名的时候没有区别，在获取内部类和数组类有区别的。
         */
        String serviceImplName = service.getClass().getCanonicalName();

        //如果注册表中有该服务就不在注册
        if(registeredService.contains(serviceImplName)){
            return ;

        }
        /**
         * 如果注册表中没有该服务就添加该服务的名称进注册表
         */
        registeredService.add(serviceImplName);


        /**
         * 可能实现多个接口 故使用class数组接收
         *
         */

        Class<?>[] interfaces = service.getClass().getInterfaces();

        if(interfaces.length==0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }

        for(Class<?> i:interfaces){
            serviceMap.put(i.getCanonicalName(),service);
        }

        logger.info("向接口：{}注册服务：{}",interfaces,serviceImplName);

    }

    /**
     *
     * @param serviceName 服务名称
     * @return
     */
    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }

        return service;
    }




}

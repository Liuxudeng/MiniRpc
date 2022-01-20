package com.mini.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡接口
 */
public interface LoadBalancer {
    /**
     * 冲一系列Instance中选择一个
     * @param instances
     * @return
     */
    Instance select(List<Instance> instances);
}

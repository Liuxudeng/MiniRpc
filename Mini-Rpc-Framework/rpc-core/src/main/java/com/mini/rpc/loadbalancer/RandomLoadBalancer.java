package com.mini.rpc.loadbalancer;


import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * 随机选择一个服务
 */
public class RandomLoadBalancer implements LoadBalancer{


    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}

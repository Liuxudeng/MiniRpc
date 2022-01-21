package com.mini.rpc.test;

import com.mini.rpc.annotation.Service;
import com.mini.rpc.api.ByeService;

/**
 * 服务实现类
 */

@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye0120,"+name;
    }
}

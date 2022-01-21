package com.mini.rpc.test;

import com.mini.rpc.annotation.Service;
import com.mini.rpc.api.HelloObject;
import com.mini.rpc.api.HelloService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class HelloServiceImpl implements HelloService {


    /**
     * 使用HelloServiceImpl初始化入职对象，方便在日志输出的时候，可以打印日志信息所属的类
     */

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);


    /**
     *
     * @param object
     * @return
     */


    @Override
    public String hello(HelloObject object) {
      //使用{}可以直接将getMessage()内容输出
        logger.info("接收到消息：{}",object.getMessage());

        return "成功调用hello()方法";

    }
}

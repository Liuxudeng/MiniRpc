package com.mini.rpc.hook;

import com.mini.rpc.util.NacosUtil;
import com.mini.rpc.factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 钩子函数
 */
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);



    /**
     * 单例模式创建狗子 保证全局只有一个钩子
     */

    private static final ShutdownHook shutdownHook = new ShutdownHook();


    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }


    //注销服务的钩子

    public void addClearAllHook(){
        logger.info("服务端关闭前将注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            NacosUtil.clearRegistry();
           ThreadPoolFactory.shutDownAll();
        }));
    }
}

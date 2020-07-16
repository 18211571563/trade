package com.trade.ResourceManager;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author georgy
 * @Date 2020-07-16 上午 10:19
 * @DESC 线程池管理
 */
@Component
public class ThreadPoolManager {

    // process 单线程执行器
    public static ExecutorService processSingleExecutorService = Executors.newSingleThreadExecutor();

    public ExecutorService getProcessSingleExecutorService(){
        return processSingleExecutorService;
    }


}

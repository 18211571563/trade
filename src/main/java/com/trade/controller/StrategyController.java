package com.trade.controller;


import com.trade.ResourceManager.ThreadPoolManager;
import com.trade.aspect.CommonAspect;
import com.trade.service.strategy.process.StrategyService;
import com.trade.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/")
public class StrategyController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StrategyService strategyService;
    @Autowired
    private ThreadPoolManager threadPoolManager;



    @GetMapping(value = "process")
    public String exec() throws InterruptedException {
        if(CommonAspect.process) throw new RuntimeException("程序运行中，请莫重复运行！");
        String traceId = LocalDateTime.now().format(TimeUtil.LONG_DATE_FORMATTER);
        threadPoolManager.getProcessSingleExecutorService().execute(() -> {
            try {
                MDC.put("traceId", traceId);
                strategyService.exec();
            } catch (Exception e) {
                logger.error("执行策略异常:{}", e);
                e.printStackTrace();
            }
        });

        return traceId;
    }

    @GetMapping(value = "ok")
    public String ok(){
        return "是否执行完成:" + String.valueOf(!CommonAspect.process) + System.lineSeparator()
                + "内存数据是否加载完成:" + String.valueOf(!CommonAspect.load);
    }

}


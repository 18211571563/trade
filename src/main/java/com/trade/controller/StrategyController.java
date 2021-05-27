package com.trade.controller;


import com.trade.ResourceManager.ThreadPoolManager;
import com.trade.aspect.CommonAspect;
import com.trade.config.TradeConstantConfig;
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
    @Autowired
    private ConfigController configController;

    @GetMapping(value = "process")
    public String exec() {
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

    @GetMapping(value = "batch/process")
    public void batchExec(){
        if(CommonAspect.process) throw new RuntimeException("程序运行中，请莫重复运行！");
        String timeCloseDay = "1~3";
        String riskParameter = "1~20";

        String[] timeCloseDaySplit = timeCloseDay.split("~");
        int timeCloseDaySplitBegin = Integer.parseInt(timeCloseDaySplit[0]);
        int timeCloseDaySplitEnd = Integer.parseInt(timeCloseDaySplit[1]);

        String[] riskParameterSplit = riskParameter.split("~");
        int riskParameterBegin = Integer.parseInt(riskParameterSplit[0]);
        int riskParameterEnd = Integer.parseInt(riskParameterSplit[1]);
        new Thread(() -> {
            for(int o = timeCloseDaySplitBegin; o <= timeCloseDaySplitEnd; o++){
                int finalO = o;
                for (int i = riskParameterBegin; i <= riskParameterEnd; i++) {
                    int finalI = i;
                    String traceId = LocalDateTime.now().format(TimeUtil.LONG_DATE_FORMATTER);
                    threadPoolManager.getProcessSingleExecutorService().execute(() -> {
                        TradeConstantConfig config = new TradeConstantConfig();
                        config.setRiskParameter(finalI);
                        config.setTimeCloseDay(finalO);
                        configController.updateConfig(config);
                        try {
                            MDC.put("traceId", traceId);
                            strategyService.exec();
                        } catch (Exception e) {
                            logger.error("执行策略异常:{}", e);
                            e.printStackTrace();
                        }
                    });
                    // 停一下，等待CommonAspect.process执行
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (CommonAspect.process){
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }).start();


    }

    @GetMapping(value = "ok")
    public String ok(){
        return "是否执行完成:" + String.valueOf(!CommonAspect.process) + System.lineSeparator()
                + "内存数据是否加载完成:" + String.valueOf(!CommonAspect.load);
    }

}


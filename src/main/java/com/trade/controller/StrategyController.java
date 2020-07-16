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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
@RequestMapping("/")
public class StrategyController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StrategyService strategyService;
    @Autowired
    private ThreadPoolManager threadPoolManager;

    @GetMapping(value = "process")
    public String process() throws InterruptedException {
        String today = LocalDate.now().format(TimeUtil.SHORT_DATE_FORMATTER);
        String startDate = LocalDate.parse(today, TimeUtil.SHORT_DATE_FORMATTER).minusYears(1).format(TimeUtil.SHORT_DATE_FORMATTER);
        return this.process(startDate , today, today, null, null);
    }

    @GetMapping(value = "process/{tsCodes}")
    public String process(@PathVariable String tsCodes) throws InterruptedException {
        String today = LocalDate.now().format(TimeUtil.SHORT_DATE_FORMATTER);
        String startDate = LocalDate.parse(today, TimeUtil.SHORT_DATE_FORMATTER).minusYears(1).format(TimeUtil.SHORT_DATE_FORMATTER);
        return this.process(startDate , today, today, false, tsCodes);
    }

    @GetMapping(value = "process/{startDate}/{endDate}")
    public String process(@PathVariable String startDate,
                          @PathVariable String endDate) throws InterruptedException {
        return this.process(startDate, endDate, endDate, null, null);
    }

    @GetMapping(value = "process/{tsCodes}/{startDate}/{endDate}")
    public String process(@PathVariable String startDate,
                          @PathVariable String endDate,
                          @PathVariable String tsCodes) throws InterruptedException {
        return this.process(startDate, endDate, endDate, false, tsCodes);
    }

    @GetMapping(value = "process/{tsCodes}/{startDate}/{endDate}/{today}/{all}")
    public String process(@PathVariable String startDate,
                          @PathVariable String endDate,
                          @PathVariable String today,
                          @PathVariable Boolean all,
                          @PathVariable String tsCodes) throws InterruptedException {
        if(CommonAspect.process) throw new RuntimeException("程序运行中，请莫重复运行！");
        String traceId = LocalDateTime.now().format(TimeUtil.LONG_DATE_FORMATTER);
        threadPoolManager.getProcessSingleExecutorService().execute(() -> {
            try {
                MDC.put("traceId", traceId);
                strategyService.process(startDate, endDate, today, all, tsCodes);
            } catch (Exception e) {
                logger.error("执行策略异常:{}", e);
                e.printStackTrace();
            }
        });

        return traceId;
    }

    @GetMapping(value = "config/update")
    public String updateConfig(TradeConstantConfig tradeConstantConfig) throws InvocationTargetException, IllegalAccessException {
        strategyService.updateConfig(tradeConstantConfig);
        return "success";
    }

    @GetMapping(value = "config/get")
    public String getConfig() throws InvocationTargetException, IllegalAccessException {
        return strategyService.getConfig();
    }

}


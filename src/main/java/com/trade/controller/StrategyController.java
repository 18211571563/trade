package com.trade.controller;


import com.trade.job.SynDataJob;
import com.trade.service.StrategyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequestMapping("/")
public class StrategyController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StrategyService strategyService;

    @GetMapping(value = "process/{startDate}/{endDate}/{today}")
    public String process(@PathVariable String startDate,
                          @PathVariable String endDate,
                          @PathVariable String today) throws InterruptedException {
        Date date = new Date();
        strategyService.process(startDate, endDate, today);
        logger.info(String.format("总耗时：%s", String.valueOf((new Date().getTime() - date.getTime()) / 1000 )) );
        return "success";
    }

    @GetMapping(value = "process/{startDate}/{endDate}/{today}/{all}/{tsCodes}")
    public String process(@PathVariable String startDate,
                          @PathVariable String endDate,
                          @PathVariable String today,
                          @PathVariable Boolean all,
                          @PathVariable String tsCodes) throws InterruptedException {
        Date date = new Date();
        strategyService.process(startDate, endDate, today, all, tsCodes);
        logger.info(String.format("总耗时：%s", String.valueOf((new Date().getTime() - date.getTime()) / 1000 )) );
        return "success";
    }

}


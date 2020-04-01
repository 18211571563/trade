package com.trade.controller;


import com.trade.service.strategy.StrategyService;
import com.trade.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;


@RestController
@RequestMapping("/")
public class StrategyController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StrategyService strategyService;

    @GetMapping(value = "process")
    public String process() throws InterruptedException {
        String today = LocalDate.now().format(TimeUtil.SHORT_DATE_FORMATTER);
        String startDate = LocalDate.parse(today, TimeUtil.SHORT_DATE_FORMATTER).minusYears(1).format(TimeUtil.SHORT_DATE_FORMATTER);
        this.process(startDate , today, today, null, null);
        return "success";
    }

    @GetMapping(value = "process/{tsCodes}")
    public String process(@PathVariable String tsCodes) throws InterruptedException {
        String today = LocalDate.now().format(TimeUtil.SHORT_DATE_FORMATTER);
        String startDate = LocalDate.parse(today, TimeUtil.SHORT_DATE_FORMATTER).minusYears(1).format(TimeUtil.SHORT_DATE_FORMATTER);
        this.process(startDate , today, today, false, tsCodes);
        return "success";
    }

    @GetMapping(value = "process/{startDate}/{endDate}")
    public String process(@PathVariable String startDate,
                          @PathVariable String endDate) throws InterruptedException {
        this.process(startDate, endDate, endDate, null, null);
        return "success";
    }

    @GetMapping(value = "process/{startDate}/{endDate}/{tsCodes}")
    public String process(@PathVariable String startDate,
                          @PathVariable String endDate,
                          @PathVariable String tsCodes) throws InterruptedException {
        this.process(startDate, endDate, endDate, false, tsCodes);
        return "success";
    }

    @GetMapping(value = "process/{startDate}/{endDate}/{today}/{all}/{tsCodes}")
    public String process(@PathVariable String startDate,
                          @PathVariable String endDate,
                          @PathVariable String today,
                          @PathVariable Boolean all,
                          @PathVariable String tsCodes) throws InterruptedException {
        strategyService.process(startDate, endDate, today, all, tsCodes);
        return "success";
    }

}


package com.trade.service.impl;

import com.BaseTest;
import com.trade.service.strategy.process.StrategyService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

public class StrategyServiceImplTest extends BaseTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    public static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private StrategyService strategyService;

    @Test
    public void exec() throws InterruptedException {
        strategyService.exec();
    }

}
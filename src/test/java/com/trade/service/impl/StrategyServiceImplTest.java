package com.trade.service.impl;

import com.BaseTest;
import com.trade.service.StrategyService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.Assert.*;

public class StrategyServiceImplTest extends BaseTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    public static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private StrategyService strategyService;

    @Test
    public void process() throws InterruptedException {
        Date date = new Date();
        strategyService.process();
        logger.info("总耗时：" + String.valueOf(new Date().getTime() - date.getTime()));

    }

}
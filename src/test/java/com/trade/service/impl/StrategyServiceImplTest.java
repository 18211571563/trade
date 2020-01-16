package com.trade.service.impl;

import com.BaseTest;
import com.trade.service.StrategyService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class StrategyServiceImplTest extends BaseTest {

    public static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private StrategyService strategyService;

    @Test
    public void process() throws InterruptedException {
        strategyService.process();
    }

}
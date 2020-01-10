package com.trade.service.impl;

import com.BaseTest;
import com.trade.service.StrategyService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class StrategyServiceImplTest extends BaseTest {

    @Autowired
    private StrategyService strategyService;

    @Test
    public void process() {
        strategyService.process();
    }
}
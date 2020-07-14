package com.trade.service.strategy;

import com.trade.config.TradeConstantConfig;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author georgy
 * @Date 2020-01-09 下午 4:31
 * @DESC TODO
 */
public interface StrategyService {

    String process(String startDate, String endDate, String today, Boolean all, String tsCodes) throws InterruptedException;

    void updateConfig(TradeConstantConfig tradeConstantConfig) throws InvocationTargetException, IllegalAccessException;

    String getConfig() throws InvocationTargetException, IllegalAccessException;
}

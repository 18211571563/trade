package com.trade.service.strategy.process;

import com.trade.config.TradeConstantConfig;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author georgy
 * @Date 2020-01-09 下午 4:31
 * @DESC TODO
 */
public interface StrategyService {

    String exec() throws InterruptedException;
}

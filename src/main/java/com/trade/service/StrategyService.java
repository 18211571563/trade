package com.trade.service;

/**
 * @Author georgy
 * @Date 2020-01-09 下午 4:31
 * @DESC TODO
 */
public interface StrategyService {
    void process() throws InterruptedException;

    void process(String tsCode);

    void process(String tsCode, String date);
}

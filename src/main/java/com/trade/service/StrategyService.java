package com.trade.service;

/**
 * @Author georgy
 * @Date 2020-01-09 下午 4:31
 * @DESC TODO
 */
public interface StrategyService {
    void process(String startDate, String endDate, String today, Boolean all, String tsCodes) throws InterruptedException;

    void process(String startDate, String endDate, String today) throws InterruptedException;

}

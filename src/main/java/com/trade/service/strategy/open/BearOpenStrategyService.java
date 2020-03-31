package com.trade.service.strategy.open;

import com.trade.vo.DailyVo;

/**
 * @Author georgy
 * @Date 2020-03-31 上午 11:47
 * @DESC 空头开仓策略
 */
public interface BearOpenStrategyService {
    void bearBreakOpen(DailyVo daily, DailyVo minOpen);
}

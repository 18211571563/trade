package com.trade.service.strategy.open;

import com.trade.vo.DailyVo;

/**
 * @Author georgy
 * @Date 2020-03-31 上午 11:46
 * @DESC 多头开仓策略
 */
public interface BullOpenStrategyService {
    void bullBreakOpen(DailyVo daily, DailyVo maxOpen);
}

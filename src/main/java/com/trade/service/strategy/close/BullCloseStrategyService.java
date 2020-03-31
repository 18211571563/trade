package com.trade.service.strategy.close;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 2:33
 * @DESC 多头平仓策略
 */
public interface BullCloseStrategyService {
    void bullBreakClose(DailyVo daily, DailyVo minClose, OrderVo orderVo);
}

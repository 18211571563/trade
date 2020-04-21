package com.trade.service.strategy.close;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;

import java.math.BigDecimal;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 2:33
 * @DESC 多头平仓策略
 */
public interface BullCloseStrategyService {
    void bullBreakClose(DailyVo daily, OrderVo orderVo, DailyVo minClose);

    void bullBreakRClose(DailyVo daily, OrderVo orderVo, BigDecimal bullClosePrice);
}

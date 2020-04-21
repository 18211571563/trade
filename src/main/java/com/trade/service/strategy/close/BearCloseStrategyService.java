package com.trade.service.strategy.close;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;

import java.math.BigDecimal;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 2:23
 * @DESC 空头平仓策略
 */
public interface BearCloseStrategyService {


    void bearBreakClose(DailyVo daily, OrderVo orderVo, DailyVo maxClose);

    void bearBreakRClose(DailyVo daily, OrderVo orderVo, BigDecimal bearClosePrice);
}

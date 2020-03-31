package com.trade.service.strategy.close;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 2:23
 * @DESC 空头平仓策略
 */
public interface BearCloseStrategyService {


    void bearBreakClose(DailyVo daily, DailyVo maxClose, OrderVo orderVo);
}

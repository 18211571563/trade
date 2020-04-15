package com.trade.service.strategy.open;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;

/**
 * @Author georgy
 * @Date 2020-03-30 上午 11:19
 * @DESC TODO
 */
public interface OpenStrategyService {

    /**
     * 突破开仓策略
     * @param daily
     * @param orderVo
     */
    void open(DailyVo daily, OrderVo orderVo);

}

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
     * 开仓策略
     * @param daily
     * @param orderVo
     * @param openStrategyCode
     */
    void open(DailyVo daily, OrderVo orderVo, String openStrategyCode);

}

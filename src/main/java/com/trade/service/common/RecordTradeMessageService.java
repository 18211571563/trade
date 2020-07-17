package com.trade.service.common;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;

import java.math.BigDecimal;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 3:32
 * @DESC 记录交易信息服务
 */
public interface RecordTradeMessageService {

    /**
     * 记录开仓信息
     * @param daily
     * @param orderVo
     */
    void logOpen(DailyVo daily, OrderVo orderVo);

    /**
     * 记录平仓信息
     * @param daily
     * @param orderVo
     */
    void logClose(DailyVo daily, OrderVo orderVo);

    /**
     * 统计
     * @param tsCode
     */
    void statistics(String tsCode);

    /**
     * 资金信息
     */
    void statisticsCapital();

    void simpleStatisticsCapital(BigDecimal frozenCapital);
}

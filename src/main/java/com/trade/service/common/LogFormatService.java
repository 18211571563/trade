package com.trade.service.common;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 3:32
 * @DESC 日志格式化服务
 */
public interface LogFormatService {

    /**
     * 记录开仓日志
     * @param logger
     * @param daily
     * @param tradeOrderVo
     */
    void logOpen(Logger logger, DailyVo daily, OrderVo tradeOrderVo);

    /**
     * 记录平仓日志
     * @param daily
     * @param orderVo
     * @param logger
     */
    void logClose(Logger logger, DailyVo daily, OrderVo orderVo);
}

package com.trade.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.trade.service.common.LogFormatService;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 3:32
 * @DESC 日志格式化服务
 */
@Service
public class LogFormatServiceImpl implements LogFormatService {

    /**
     * 记录开仓日志
     * @param logger
     * @param daily
     * @param tradeOrderVo
     */
    @Override
    public void logOpen(Logger logger, DailyVo daily, OrderVo tradeOrderVo) {
        String direction = "未知";
        if(tradeOrderVo.getDirection() == 0){
            direction = "空头";
        }else if(tradeOrderVo.getDirection() == 1){
            direction = "多头";
        }

        logger.info("交易 - 标的:{}, 方向:{}, 价格:{}, 交易量:{}, 交易日:{}, 数据:{}",
                tradeOrderVo.getTsCode(),
                direction,
                tradeOrderVo.getPrice(),
                tradeOrderVo.getVolume(),
                daily.getTrade_date(),
                JSON.toJSONString(tradeOrderVo));
    }

    /**
     * 记录平仓日志
     * @param daily
     * @param orderVo
     * @param logger
     */
    @Override
    public void logClose(Logger logger, DailyVo daily, OrderVo orderVo) {
        String direction = "未知";
        double bp = 0; // 损益
        double bp_rate = 0; // 损益比例

        if(orderVo.getDirection() == 0){
            direction = "平空";
            bp = orderVo.getPrice().subtract(new BigDecimal(daily.getClose())).multiply(orderVo.getVolume()).doubleValue();
            bp_rate = orderVo.getPrice().subtract(new BigDecimal(daily.getClose())).divide(orderVo.getPrice(), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }else if(orderVo.getDirection() == 1){
            direction = "平多";
            bp = new BigDecimal(daily.getClose()).subtract(orderVo.getPrice()).multiply(orderVo.getVolume()).doubleValue();
            bp_rate = new BigDecimal(daily.getClose()).subtract(orderVo.getPrice()).divide(orderVo.getPrice(), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        logger.info("止损 - 标的:{}, 方向:{}, 损益:{},损益比例:{},交易日:{}, 开仓价格:{}, 平仓价格: {}, 交易量:{},  数据:{}",
                orderVo.getTsCode(),
                direction,
                bp,
                bp_rate,
                daily.getTrade_date(),
                orderVo.getPrice(),
                daily.getClose(),
                orderVo.getVolume(),
                JSON.toJSONString(orderVo));
    }

}

package com.trade.service.strategy.close.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.service.strategy.close.BullCloseStrategyService;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 2:34
 * @DESC 多头平仓策略
 */
@Service
public class BullCloseStrategyServiceImpl implements BullCloseStrategyService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private TradeService tradeService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void bullBreakClose(DailyVo daily, OrderVo orderVo, DailyVo minClose){
        // 判断是否持有多头头寸
        if(orderVo != null && orderVo.getDirection() == 1){
            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(minClose.getClose())) < 0){
                tradeService.close(daily, orderVo);
            }else if(
                    tradeConstantConfig.getUseTimeClose() &&
                            LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER).compareTo(orderVo.getTime().plusDays(tradeConstantConfig.getTimeCloseDay())) > 0 && // 当前时间超过7天
                            new BigDecimal(daily.getClose()).compareTo(orderVo.getPrice()) < 0 // 亏损 = 当前价小于交易价
            ){
                // 如果超过时间还是亏损，则平仓
                tradeService.close(daily, orderVo);
            }
        }
    }

    @Override
    public void bullBreakRClose(DailyVo daily, OrderVo orderVo, BigDecimal bullClosePrice) {
        // 判断是否持有多头头寸
        if(orderVo != null && orderVo.getDirection() == 1){
            // 如果当前价小于止损价，进行平仓
            if(new BigDecimal(daily.getClose()).compareTo(bullClosePrice) < 0) {
                tradeService.close(daily, orderVo);
            }else if(
                    tradeConstantConfig.getUseTimeClose() &&
                    LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER).compareTo(orderVo.getTime().plusDays(tradeConstantConfig.getTimeCloseDay())) > 0 && // 当前时间超过7天
                    new BigDecimal(daily.getClose()).compareTo(orderVo.getPrice()) < 0 // 亏损 = 当前价小于交易价
            ){
                // 如果超过时间还是亏损，则平仓
                tradeService.close(daily, orderVo);
            }
        }
    }

}

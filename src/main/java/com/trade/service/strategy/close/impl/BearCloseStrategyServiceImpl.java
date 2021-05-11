package com.trade.service.strategy.close.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.service.strategy.close.BearCloseStrategyService;
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
 * @Date 2020-03-31 下午 2:24
 * @DESC 空头平仓策略
 */
@Service
public class BearCloseStrategyServiceImpl implements BearCloseStrategyService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private TradeService tradeService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void bearBreakClose(DailyVo daily, OrderVo orderVo, DailyVo maxClose){
        // 判断是否持有空头头寸
        if(orderVo != null && orderVo.getDirection() == 0){
            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxClose.getClose())) > 0){
                tradeService.close(daily, orderVo);
            }
        }
    }

    @Override
    public void bearBreakRClose(DailyVo daily, OrderVo orderVo, BigDecimal bearClosePrice) {
        // 判断是否持有空头头寸
        if(orderVo != null && orderVo.getDirection() == 0){
            if(new BigDecimal(daily.getClose()).compareTo(bearClosePrice) > 0){
                tradeService.close(daily, orderVo);
            }else if(
                    LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER).compareTo(orderVo.getTime().plusDays(7)) > 0 && // 当前时间超过7天
                            new BigDecimal(daily.getClose()).compareTo(orderVo.getPrice()) > 0 // 亏损 = 当前价大于交易价
            ){
                // 如果超过时间还是亏损，则平仓
                tradeService.close(daily, orderVo);
            }
        }
    }

}

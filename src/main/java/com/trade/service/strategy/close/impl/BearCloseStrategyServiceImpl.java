package com.trade.service.strategy.close.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.service.strategy.close.BearCloseStrategyService;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
    public void bearBreakClose(DailyVo daily, DailyVo maxClose, OrderVo orderVo){

        // 判断是否持有空头头寸
        if(orderVo != null && orderVo.getDirection() == 0){

            // 判断是否当前价大于突破价，如果是，进行平空操作
            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxClose.getClose())) > 0){
                tradeService.close(daily, orderVo, tradeConstantConfig.getUsedCapitail());
            }
        }
    }

}

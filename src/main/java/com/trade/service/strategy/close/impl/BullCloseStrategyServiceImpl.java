package com.trade.service.strategy.close.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.service.strategy.close.BullCloseStrategyService;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
    public void bullBreakClose(DailyVo daily, DailyVo minClose, OrderVo orderVo){

        // 判断是否持有多头头寸
        if(orderVo != null && orderVo.getDirection() == 1){

            // 判断是否当前价小于突破价，如果是，进行平多操作
            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(minClose.getClose())) < 0){
                tradeService.close(daily, orderVo, tradeConstantConfig.getUsedCapitail());
            }
        }
    }

}

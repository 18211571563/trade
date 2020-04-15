package com.trade.service.strategy.close.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.strategy.close.BearCloseStrategyService;
import com.trade.service.strategy.close.BullCloseStrategyService;
import com.trade.service.strategy.close.CloseStrategyService;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.utils.CapitalUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-03-30 上午 11:35
 * @DESC 止损策略
 */
@Service
public class CloseStrategyServiceImpl implements CloseStrategyService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private DataService dataService;
    @Autowired
    private BearCloseStrategyService bearCloseStrategyService;
    @Autowired
    private BullCloseStrategyService bullCloseStrategyService;
    @Autowired
    private TradeService tradeService;

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 止损策略
     * @param daily
     * @param orderVo
     */
    public void close(DailyVo daily, OrderVo orderVo) {
        /***************************************************************** 是否容许止损 ************************************************************************/
        if(!tradeService.allowClose(daily, orderVo)) return;

        /***************************************************************** 止损策略逻辑 ************************************************************************/
        this.breakClose(daily, orderVo);

    }

    /**
     * 突破止损策略
     * @param daily
     * @param orderVo
     */
    private void breakClose(DailyVo daily, OrderVo orderVo) {
        // 计算突破点
        List<DailyVo> breakCloseDailyVo = dataService.daily(daily.getTs_code(), daily.getTrade_date(), tradeConstantConfig.getBreakCloseDay());
        DailyVo maxClose = CapitalUtil.getMax(breakCloseDailyVo);
        DailyVo minClose = CapitalUtil.getMin(breakCloseDailyVo);

        if(orderVo.getDirection() == 0){ // 空头止损
            bearCloseStrategyService.bearBreakClose(daily, maxClose, orderVo);

        }else if(orderVo.getDirection() == 1){ // 多头止损
            bullCloseStrategyService.bullBreakClose(daily, minClose, orderVo);

        }
    }

}

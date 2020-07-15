package com.trade.service.strategy.close.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.StrategyConstantConfig;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.CalculateService;
import com.trade.service.strategy.close.BearCloseStrategyService;
import com.trade.service.strategy.close.BullCloseStrategyService;
import com.trade.service.strategy.close.CloseStrategyService;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.utils.CapitalUtil;
import com.trade.utils.TimeUtil;
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

    @Autowired
    private CalculateService calculateService;

    /**
     * 止损策略
     * @param daily
     * @param orderVo
     */
    @Override
    public void close(DailyVo daily, OrderVo orderVo, String closeStrategyCode) {
        /***************************************************************** 是否容许止损 ************************************************************************/
        if(!tradeService.allowClose(daily, orderVo)) return;

        /***************************************************************** 止损策略逻辑 ************************************************************************/
        if(tradeService.selectCloseStrategy("breakClose").equals(closeStrategyCode)){
            // 突破 策略
            this.breakClose(daily, orderVo);
        }if(tradeService.selectCloseStrategy("breakRClose").equals(closeStrategyCode)){
            // 突破波动率(R) 策略
            this.breakRClose(daily, orderVo);
        }else{
            throw new RuntimeException("没有可用的止损策略");
        }


    }

    /**
     * 突破 策略
     * @param daily
     * @param orderVo
     */
    private void breakClose(DailyVo daily, OrderVo orderVo) {
        /** ################################## 计算突破价格 ##################################### **/
        List<DailyVo> breakCloseDailyVo = dataService.daily(daily.getTs_code(), daily.getTrade_date(), tradeConstantConfig.getBreakCloseDay());
        DailyVo maxClose = CapitalUtil.getMax(breakCloseDailyVo);
        DailyVo minClose = CapitalUtil.getMin(breakCloseDailyVo);

        /** ################################## 止损 ##################################### **/
        if(orderVo.getDirection() == 0){ // 空头止损
            bearCloseStrategyService.bearBreakClose(daily, orderVo, maxClose);

        }else if(orderVo.getDirection() == 1){ // 多头止损
            bullCloseStrategyService.bullBreakClose(daily, orderVo, minClose);

        }
    }

    /**
     * 突破波动率(R) 策略
     * @param daily
     * @param orderVo
     */
    private void breakRClose(DailyVo daily, OrderVo orderVo) {
        /** ################################## 计算 多空 止损价格 ##################################### **/
        /** 计算R **/
        BigDecimal R = null;
        {
            // 计算ATR
            BigDecimal atr = calculateService.getDailyAverageAtr(orderVo.getTsCode(), orderVo.getTime().format(TimeUtil.SHORT_DATE_FORMATTER), tradeConstantConfig.getAtrPeriod()); // 获取今日 ATR
            // 计算R = atr * closeDeep
            R = atr.multiply(BigDecimal.valueOf(tradeConstantConfig.getCloseDeep())).setScale(2, BigDecimal.ROUND_HALF_UP);

        }

        /** 获取开仓时间到目前时间的信息 **/
        List<DailyVo> dailyTradeRecords = dataService.daily(orderVo.getTsCode(), orderVo.getTime().format(TimeUtil.SHORT_DATE_FORMATTER), daily.getTrade_date());

        /** ################################## 止损 ##################################### **/
        if(orderVo.getDirection() == 0){ // 空头止损
            // 计算开仓时间到目前时间的最低价
            DailyVo minClose = CapitalUtil.getMin(dailyTradeRecords);
            // 空头止损价
            BigDecimal bearClosePrice = new BigDecimal(minClose.getClose()).add(R);

            bearCloseStrategyService.bearBreakRClose(daily, orderVo, bearClosePrice);

        }else if(orderVo.getDirection() == 1){ // 多头止损
            // 计算开仓时间到目前时间的最高价
            DailyVo maxClose = CapitalUtil.getMax(dailyTradeRecords);
            // 多头止损价
            BigDecimal bullClosePrice = new BigDecimal(maxClose.getClose()).subtract(R);

            bullCloseStrategyService.bullBreakRClose(daily, orderVo, bullClosePrice);

        }


    }

}

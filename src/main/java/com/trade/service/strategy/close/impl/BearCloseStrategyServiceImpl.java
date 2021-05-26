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
import java.util.List;

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
    @Autowired
    private DataService dataService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void bearBreakClose(DailyVo daily, OrderVo orderVo, DailyVo maxClose){
        // 判断是否持有空头头寸
        if(orderVo != null && orderVo.getDirection() == 0){
            // 价格止损
            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxClose.getClose())) > 0){
                tradeService.close(daily, orderVo);
                return;
            }

            // 时间止损1
            if(
                    tradeConstantConfig.getUseTimeClose() &&
                    LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER).compareTo(orderVo.getTime().plusDays(tradeConstantConfig.getTimeCloseDay())) > 0 && // 当前时间超过7天
                    new BigDecimal(daily.getClose()).compareTo(orderVo.getPrice()) > 0 // 亏损 = 当前价大于交易价
            ){
                // 如果超过时间还是亏损，则平仓
                tradeService.close(daily, orderVo);
                return;
            }

            // 时间止损2
            List<DailyVo> dailyList = dataService.daily(daily.getTs_code(), daily.getTrade_date(), 30); // 获取最近M天的行情
            BigDecimal c = BigDecimal.ZERO;
            if(dailyList != null && dailyList.size() != 0){
                if(LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER).compareTo(orderVo.getTime().plusDays(30)) > 0) return;
                dailyList.add(0, daily);
                BigDecimal nowClose = new BigDecimal(dailyList.get(dailyList.size() - 1).getClose());
                for (DailyVo dailyVo : dailyList) {
                    c = c.add((new BigDecimal(dailyVo.getClose()).subtract(nowClose)).divide(nowClose, 4, BigDecimal.ROUND_HALF_UP)); // (价格 - 今日行情价格)/今日行情价格
                }
                if(c.compareTo(BigDecimal.valueOf(0.02)) < 0 ){
                    // 如果超过时间还是亏损，则平仓
                    tradeService.close(daily, orderVo);
                    return;
                }
            }


        }
    }

    @Override
    public void bearBreakRClose(DailyVo daily, OrderVo orderVo, BigDecimal bearClosePrice) {
        // 判断是否持有空头头寸
        if(orderVo != null && orderVo.getDirection() == 0){

            // 价格止损
            if(new BigDecimal(daily.getClose()).compareTo(bearClosePrice) > 0){
                tradeService.close(daily, orderVo);
                return;
            }

            // 时间止损1
            if(
                    tradeConstantConfig.getUseTimeClose() &&
                    LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER).compareTo(orderVo.getTime().plusDays(tradeConstantConfig.getTimeCloseDay())) > 0 && // 当前时间超过7天
                    new BigDecimal(daily.getClose()).compareTo(orderVo.getPrice()) > 0 // 亏损 = 当前价大于交易价
            ){
                // 如果超过时间还是亏损，则平仓
                tradeService.close(daily, orderVo);
                return;
            }

            // 时间止损2
            List<DailyVo> dailyList = dataService.daily(daily.getTs_code(), daily.getTrade_date(), 30); // 获取最近M天的行情
            BigDecimal c = BigDecimal.ZERO;
            if(dailyList != null && dailyList.size() != 0){
                if(LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER).compareTo(orderVo.getTime().plusDays(30)) > 0) return;
                dailyList.add(0, daily);
                BigDecimal nowClose = new BigDecimal(dailyList.get(dailyList.size() - 1).getClose());
                for (DailyVo dailyVo : dailyList) {
                    c = c.add((new BigDecimal(dailyVo.getClose()).subtract(nowClose)).divide(nowClose, 4, BigDecimal.ROUND_HALF_UP)); // (价格 - 今日行情价格)/今日行情价格
                }
                if(c.compareTo(BigDecimal.valueOf(0.02)) < 0 ){
                    // 如果超过时间还是亏损，则平仓
                    tradeService.close(daily, orderVo);
                    return;
                }
            }


        }
    }

}

package com.trade.service.strategy.open.impl;

import com.alibaba.fastjson.JSON;
import com.trade.capital.CapitalManager;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.CalculateService;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.service.strategy.open.BearOpenStrategyService;
import com.trade.utils.CapitalUtil;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author georgy
 * @Date 2020-03-31 上午 11:47
 * @DESC 空头开仓策略
 */
@Service
public class BearOpenStrategyServiceImpl implements BearOpenStrategyService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private DataService dataService;
    @Autowired
    private CalculateService calculateService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private CapitalManager capitalManager;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void bearBreakOpen(DailyVo daily, DailyVo minOpen){

        String tsCode = daily.getTs_code();
        String date = daily.getTrade_date();

        if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(minOpen.getClose())) < 0){
            // 计算交易量
            BigDecimal atr = calculateService.getDailyAverageAtr(tsCode, date, tradeConstantConfig.getAtrPeriod()); // 获取今日 ATR
            int tradeVolume = CapitalUtil.getTradeVolume(capitalManager.getTotalCapital(), capitalManager.getRiskParameter(), BigDecimal.valueOf(tradeConstantConfig.getCloseDeep()), atr, tradeConstantConfig.getUnit());

            OrderVo tradeOrderVo = new OrderVo(daily.getTs_code(),
                    0,
                    new BigDecimal(daily.getClose()),
                    BigDecimal.valueOf(tradeVolume * tradeConstantConfig.getUnit()),
                    LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER));
            tradeService.open(daily, tradeOrderVo);
        }
    }

}

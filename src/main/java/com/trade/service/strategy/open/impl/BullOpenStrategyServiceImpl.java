package com.trade.service.strategy.open.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.CalculateService;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.service.strategy.open.BullOpenStrategyService;
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
 * @Date 2020-03-31 上午 11:46
 * @DESC 多头开仓策略
 */
@Service
public class BullOpenStrategyServiceImpl implements BullOpenStrategyService {


    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private DataService dataService;
    @Autowired
    private CalculateService calculateService;
    @Autowired
    private TradeService tradeService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void bullBreakOpen(DailyVo daily, DailyVo maxOpen){

        String tsCode = daily.getTs_code();
        String date = daily.getTrade_date();

        if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxOpen.getClose())) > 0){
            // 计算交易量
            BigDecimal atr = calculateService.getDailyAverageAtr(tsCode, date, tradeConstantConfig.getAtrPeriod()); // 获取今日 ATR
            int tradeVolume = CapitalUtil.getTradeVolume(tradeService.getTotalCapital(), tradeService.getRiskParameter(), BigDecimal.valueOf(tradeConstantConfig.getCloseDeep()), atr, tradeConstantConfig.getUnit());

            OrderVo tradeOrderVo = new OrderVo(daily.getTs_code(),
                    1,
                    new BigDecimal(daily.getClose()),
                    BigDecimal.valueOf(tradeVolume * tradeConstantConfig.getUnit()),
                    LocalDate.parse(daily.getTrade_date(), TimeUtil.SHORT_DATE_FORMATTER));
            tradeService.open(daily, tradeOrderVo);

        }
    }




}

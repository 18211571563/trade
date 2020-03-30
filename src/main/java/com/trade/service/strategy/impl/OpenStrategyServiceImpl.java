package com.trade.service.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.CalculateService;
import com.trade.service.common.DataService;
import com.trade.service.strategy.OpenStrategyService;
import com.trade.service.common.TradeService;
import com.trade.utils.CapitalUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-03-30 上午 11:20
 * @DESC TODO
 */
@Service
public class OpenStrategyServiceImpl implements OpenStrategyService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private DataService dataService;
    @Autowired
    private CalculateService calculateService;
    @Autowired
    private TradeService tradeService;

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger tradeLogger = LoggerFactory.getLogger("trade");
    Logger todayTradeLogger = LoggerFactory.getLogger("todayTrade");
    Logger assetLogger = LoggerFactory.getLogger("asset");


    /**
     * 突破开仓策略
     * @param daily
     * @param orderVo
     */
    @Override
    public void breakOpen( DailyVo daily, OrderVo orderVo) {
        String tsCode = daily.getTs_code();
        String date = daily.getTrade_date();

        // 计算突破点
        List<DailyVo> breakOpenDailyVo = dataService.daily(tsCode, date, tradeConstantConfig.getBreakOpenDay());
        DailyVo maxOpen = CapitalUtil.getMax(breakOpenDailyVo);
        DailyVo minOpen = CapitalUtil.getMin(breakOpenDailyVo);

        if(orderVo == null){
            // 获取过滤线趋势
            BigDecimal filterTrend = calculateService.getFilterTrend(tsCode, date, tradeConstantConfig.getFilterDay());
            if(filterTrend.compareTo(BigDecimal.ZERO) == 0) tradeLogger.info("过滤线无方向(0),不进行开仓!");

            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxOpen.getClose())) > 0){
                if(filterTrend.compareTo(BigDecimal.ZERO) > 0){
                    // 计算交易量
                    BigDecimal atr = calculateService.getDailyAverageAtr(tsCode, date, tradeConstantConfig.getAtrPeriod()); // 获取今日 ATR
                    int tradeVolume = CapitalUtil.getTradeVolume(tradeService.getTotalCapital(), tradeService.getRiskParameter(), atr, tradeConstantConfig.getUnit());

                    OrderVo tradeOrderVo = new OrderVo(daily.getTs_code(),
                            1,
                            new BigDecimal(daily.getClose()),
                            BigDecimal.valueOf(tradeVolume * tradeConstantConfig.getUnit()),
                            LocalDateTime.now());
                    tradeService.open(tradeOrderVo, tradeConstantConfig.getUsedCapitail());

                    tradeLogger.info("交易 - 开多:{}, 价格:{}, 交易量:{}, 交易日:{}, 数据:{}" ,
                            tradeOrderVo.getTsCode(),
                            tradeOrderVo.getPrice(),
                            tradeOrderVo.getVolume(),
                            date ,
                            JSON.toJSONString(tradeOrderVo));

                    if(date.equals(tradeConstantConfig.getToday())){ // 记录今天的交易日志
                        todayTradeLogger.info("交易 - 开多:{}, 价格:{}, 交易量:{}, 交易日:{}, 数据:{}" ,
                                tradeOrderVo.getTsCode(),
                                tradeOrderVo.getPrice(),
                                tradeOrderVo.getVolume(),
                                date ,
                                JSON.toJSONString(tradeOrderVo));
                    }


                }

            }else if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(minOpen.getClose())) < 0){
                if(filterTrend.compareTo(BigDecimal.ZERO) < 0){
                    // 计算交易量
                    BigDecimal atr = calculateService.getDailyAverageAtr(tsCode, date, tradeConstantConfig.getAtrPeriod()); // 获取今日 ATR
                    int tradeVolume = CapitalUtil.getTradeVolume(tradeService.getTotalCapital(), tradeService.getRiskParameter(), atr, tradeConstantConfig.getUnit());

                    OrderVo tradeOrderVo = new OrderVo(daily.getTs_code(),
                            0,
                            new BigDecimal(daily.getClose()),
                            BigDecimal.valueOf(tradeVolume * tradeConstantConfig.getUnit()),
                            LocalDateTime.now());
                    tradeService.open(tradeOrderVo, tradeConstantConfig.getUsedCapitail());
                    tradeLogger.info("交易 - 开空:{}, 价格:{}, 交易量:{}, 交易日:{}, 数据:{}" ,
                            tradeOrderVo.getTsCode(),
                            tradeOrderVo.getPrice(),
                            tradeOrderVo.getVolume(),
                            date ,
                            JSON.toJSONString(tradeOrderVo));

                    if(date.equals(tradeConstantConfig.getToday())){ // 记录今天的交易日志
                        todayTradeLogger.info("交易 - 开空:{}, 价格:{}, 交易量:{}, 交易日:{}, 数据:{}" ,
                                tradeOrderVo.getTsCode(),
                                tradeOrderVo.getPrice(),
                                tradeOrderVo.getVolume(),
                                date ,
                                JSON.toJSONString(tradeOrderVo));
                    }
                }

            }else{
                logger.info("交易 - 无  ，交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
            }

        }else{
            logger.info("已经存在仓位无需交易, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
        }
    }

}

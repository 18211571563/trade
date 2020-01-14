package com.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.trade.service.CalculateService;
import com.trade.service.DataService;
import com.trade.service.StrategyService;
import com.trade.service.TradeService;
import com.trade.utils.CapitalUtil;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author georgy
 * @Date 2020-01-09 下午 4:32
 * @DESC TODO
 */
@Service
public class StrategyServiceImpl implements StrategyService {

    private static String tsCode = "000100.SZ";
    private static int unit = 100;
    private static String startDate = "20190101";
    private static String endDate = "20191231";
    private static Map<String, String>  assetMap; // 资产

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger tradeLogger = LoggerFactory.getLogger("trade");
    Logger asset = LoggerFactory.getLogger("asset");

    @Autowired
    private DataService dataService;
    @Autowired
    private CalculateService calculateService;
    @Autowired
    private TradeService tradeService;

    @Override
    public void process(){
        /***************************************************************** for *********************************************************************/
        LocalDate startDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER);
        LocalDate endDateL = LocalDate.parse(endDate, TimeUtil.SHORT_DATE_FORMATTER);
        LocalDate dateL = startDateL;
        for(int i = 0; endDateL.compareTo(dateL) >= 0; dateL = dateL.plusDays(1)){
            String date = dateL.format(TimeUtil.SHORT_DATE_FORMATTER);
            if(dataService.tradeCal(date)){
                this.process(tsCode, date);
            }else{
                logger.warn("非交易日:{}", date);
            }
        }
        asset.info(TradeService.assetVo.getTotalCapital().toString());
    }

    @Override
    public void process(String tsCode, String date){
        // 获取今日行情
        DailyVo daily = dataService.daily(tsCode, date, date).get(0);
        // 获取仓位信息
        OrderVo orderVo = tradeService.getOrderVo(tsCode);

        /***************************************************************** open ************************************************************************/
        // 计算突破点
        List<DailyVo> breakOpenDailyVo = dataService.daily(tsCode, date, TradeService.breakOpenDay);
        DailyVo maxOpen = CapitalUtil.getMax(breakOpenDailyVo);
        DailyVo minOpen = CapitalUtil.getMin(breakOpenDailyVo);

        if(orderVo == null){
            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxOpen.getClose())) > 0){
                // 计算交易量
                BigDecimal atr = calculateService.getDailyAverageAtr(tsCode, date, TradeService.atrPeriod); // 获取今日 ATR
                int tradeVolume = CapitalUtil.getTradeVolume(tradeService.getTotalCapital(), tradeService.getRiskParameter(), atr, unit);

                OrderVo tradeOrderVo = new OrderVo(daily.getTs_code(),
                                                1,
                                                new BigDecimal(daily.getClose()),
                                                BigDecimal.valueOf(tradeVolume * unit),
                                                LocalDateTime.now());
                tradeService.open(tradeOrderVo);
                tradeLogger.info("交易 - 开多, 价格:{}, 交易量:{}, 交易日:{}, 数据:{}" ,
                        tradeOrderVo.getPrice(),
                        tradeOrderVo.getVolume(),
                        date ,
                        JSON.toJSONString(tradeOrderVo));

            }else if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(minOpen.getClose())) < 0){
                // 计算交易量
                BigDecimal atr = calculateService.getDailyAverageAtr(tsCode, date, TradeService.atrPeriod); // 获取今日 ATR
                int tradeVolume = CapitalUtil.getTradeVolume(tradeService.getTotalCapital(), tradeService.getRiskParameter(), atr, unit);

                OrderVo tradeOrderVo = new OrderVo(daily.getTs_code(),
                                                0,
                                                new BigDecimal(daily.getClose()),
                                                BigDecimal.valueOf(tradeVolume * unit),
                                                LocalDateTime.now());
                tradeService.open(tradeOrderVo);
                tradeLogger.info("交易 - 开空, 价格:{}, 交易量:{}, 交易日:{}, 数据:{}" ,
                        tradeOrderVo.getPrice(),
                        tradeOrderVo.getVolume(),
                        date ,
                        JSON.toJSONString(tradeOrderVo));

            }else{
                logger.info("交易 - 无  ，交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
            }

        }else{
            logger.info("已经存在仓位无需交易, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
        }


        /***************************************************************** 止损 ************************************************************************/
        // 计算突破点
        List<DailyVo> breakCloseDailyVo = dataService.daily(tsCode, date, TradeService.breakCloseDay);
        DailyVo maxClose = CapitalUtil.getMax(breakCloseDailyVo);
        DailyVo minClose = CapitalUtil.getMin(breakCloseDailyVo);

        if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxClose.getClose())) > 0){ // 判断是否当前价大于突破价，如果是，进行平空操作
            // 判断是否持有空头头寸
            if(orderVo != null){
                if(orderVo.getDirection() == 0){
                    tradeService.close(orderVo, new BigDecimal(daily.getClose()));

                    tradeLogger.info("止损 - 平空, 开仓价格:{}, 平仓价格: {}, 交易量:{}, 交易日:{}, 数据:{}" ,
                            orderVo.getPrice(),
                            daily.getClose(),
                            orderVo.getVolume(),
                            date ,
                            JSON.toJSONString(orderVo));

                }else{
                    logger.info("止损 - 没有 空头 头寸无需止损, 交易日:{}, 数据:{}",date , JSON.toJSONString(daily));
                }
            }else{
                logger.info("止损 - 没有头寸无需止损, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
            }

        }else if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(minClose.getClose())) < 0){ // 判断是否当前价小于突破价，如果是，进行平多操作
            // 判断是否持有多头头寸
            if(orderVo != null){
                if(orderVo.getDirection() == 1){
                    tradeService.close(orderVo, new BigDecimal(daily.getClose()));
                    tradeLogger.info("止损 - 平多, 开仓价格:{}, 平仓价格: {}, 交易量:{}, 交易日:{}, 数据:{}" ,
                            orderVo.getPrice(),
                            daily.getClose(),
                            orderVo.getVolume(),
                            date ,
                            JSON.toJSONString(orderVo));

                }else{
                    logger.info("止损 - 没有 多头 头寸无需止损, 交易日:{}, 数据:{}",date , JSON.toJSONString(daily));
                }
            }else{
                logger.info("止损 - 没有头寸无需止损, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
            }
        }




//        // 获取滤镜，判断当前的开仓信号是否与长期趋势背离，如背离，终止交易



    }







}



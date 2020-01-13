package com.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.trade.service.CalculateService;
import com.trade.service.DataService;
import com.trade.service.StrategyService;
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
    private static Map<String, String>  assetMap; // 资产
    private static BigDecimal totalCapital = BigDecimal.valueOf(100000.00); // 总资金
    private static BigDecimal riskParameter; // 风险系数
    private static String startDate = "20190101";
    private static String endDate = "20190201";
    private static int atrPeriod = 30;
    private static int breakOpenDay = 50;
    private static int breakCloseDay = 25;

    private static List<OrderVo> tradeOrders = new ArrayList<>();


    Logger logger = LoggerFactory.getLogger(getClass());
    Logger tradeLogger = LoggerFactory.getLogger("trade");

    @Autowired
    private DataService dataService;
    @Autowired
    private CalculateService calculateService;

    @Override
    public void process(){
        /***************************************************************** for *********************************************************************/
        LocalDate startDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER);
        LocalDate endDateL = LocalDate.parse(endDate, TimeUtil.SHORT_DATE_FORMATTER);
        LocalDate dateL = startDateL;
        for(int i = 0; endDateL.compareTo(dateL) >= 0; dateL = dateL.plusDays(1)){
            String date = dateL.format(TimeUtil.SHORT_DATE_FORMATTER);
            if(dataService.tradeCal(date)){
                this.process(date);
            }else{
                logger.warn("非交易日:{}", date);
            }
        }
    }

    @Override
    public void process(String date){
        // 获取今日行情
        DailyVo daily = dataService.daily(tsCode, date, date).get(0);
        // 获取仓位信息
        OrderVo orderVo = calculateService.getOrder(tradeOrders, tsCode);

        /***************************************************************** open ************************************************************************/
        // 计算突破点
        List<DailyVo> breakOpenDailyVo = dataService.daily(tsCode, date, breakOpenDay);
        DailyVo maxOpen = CapitalUtil.getMax(breakOpenDailyVo);
        DailyVo minOpen = CapitalUtil.getMin(breakOpenDailyVo);

        if(orderVo == null){
            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxOpen.getClose())) > 0){
                tradeLogger.info("交易 - 开多, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
                OrderVo tradeOrderVo = new OrderVo(daily.getTs_code(),
                                                1,
                                                new BigDecimal(daily.getClose()),
                                                new BigDecimal("1"),
                                                LocalDateTime.now());
                tradeOrders.add(tradeOrderVo);

            }else if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(minOpen.getClose())) < 0){
                tradeLogger.info("交易 - 开空, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
                OrderVo tradeOrderVo = new OrderVo(daily.getTs_code(),
                                                0,
                                                new BigDecimal(daily.getClose()),
                                                new BigDecimal("1"),
                                                LocalDateTime.now());
                tradeOrders.add(tradeOrderVo);

            }else{
                logger.info("交易 - 无  ，交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
            }

        }else{
            logger.info("已经存在仓位无需交易, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
        }


        /***************************************************************** close ************************************************************************/
        // 计算突破点
        List<DailyVo> breakCloseDailyVo = dataService.daily(tsCode, date, breakCloseDay);
        DailyVo maxClose = CapitalUtil.getMax(breakCloseDailyVo);
        DailyVo minClose = CapitalUtil.getMin(breakCloseDailyVo);

        if(orderVo != null){

            if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxClose.getClose())) > 0){ // 判断是否当前价大于突破价，如果是，进行平空操作
                // 判断是否持有空头头寸
                if(orderVo.getDirection() == 0){
                    tradeLogger.info("止损 - 平空, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
                    tradeOrders.remove(orderVo);

                }else{
                    logger.info("止损 - 没有持有空头头寸, 交易日:{}, 数据:{}",date , JSON.toJSONString(daily));
                }

            }else if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(minClose.getClose())) < 0){ // 判断是否当前价小于突破价，如果是，进行平多操作
                // 判断是否持有多头头寸
                if(orderVo.getDirection() == 1){
                    tradeLogger.info("止损 - 平头, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
                    tradeOrders.remove(orderVo);

                }else{
                    logger.info("止损 - 没有持有多头头寸, 交易日:{}, 数据:{}",date , JSON.toJSONString(daily));
                }
            }

        }else{
            logger.info("没有仓位无需止损, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
        }











        //        // 根据当前信息判断是否有止损信号
//
//        // 如果止损并且持有仓位，进行平仓操作

//        /***************************************************************** 准备基础数据 *********************************************************************/
//        // 获取 startDate -> endDate 数据
//        List<DailyVo> dailys = dataService.daily(tsCode, startDate, date);
//
//        /***************************************************************** 计算atr *********************************************************************/
//        List<BigDecimal> dailyAtrs = calculateService.getDailyAtrs(tsCode, dailys, date, atrPeriod);
//
//
//
//
//
//
//        // 获取滤镜，判断当前的开仓信号是否与长期趋势背离，如背离，终止交易
//
//        // 如开仓，根据atr计算 下注手数
//
//



    }







}



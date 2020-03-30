package com.trade.service.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.strategy.CloseStrategyService;
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
 * @DESC TODO
 */
@Service
public class CloseStrategyServiceImpl implements CloseStrategyService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private DataService dataService;
    @Autowired
    private TradeService tradeService;

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger tradeLogger = LoggerFactory.getLogger("trade");
    Logger todayTradeLogger = LoggerFactory.getLogger("todayTrade");
    Logger assetLogger = LoggerFactory.getLogger("asset");

    /**
     * 突破止损策略
     * @param daily
     * @param orderVo
     */
    public void breakClose(DailyVo daily, OrderVo orderVo) {
        String tsCode = daily.getTs_code();
        String date = daily.getTrade_date();

        // 计算突破点
        List<DailyVo> breakCloseDailyVo = dataService.daily(tsCode, date, tradeConstantConfig.getBreakCloseDay());
        DailyVo maxClose = CapitalUtil.getMax(breakCloseDailyVo);
        DailyVo minClose = CapitalUtil.getMin(breakCloseDailyVo);

        if(new BigDecimal(daily.getClose()).compareTo(new BigDecimal(maxClose.getClose())) > 0){ // 判断是否当前价大于突破价，如果是，进行平空操作
            // 判断是否持有空头头寸
            if(orderVo != null){
                if(orderVo.getDirection() == 0){
                    tradeService.close(orderVo, new BigDecimal(daily.getClose()), tradeConstantConfig.getUsedCapitail());

                    tradeLogger.info("止损 - 平空:{}, 损益:{},损益比例:{},交易日:{}, 开仓价格:{}, 平仓价格: {}, 交易量:{},  数据:{}" ,
                            orderVo.getTsCode(),
                            orderVo.getPrice().subtract(new BigDecimal(daily.getClose())).multiply(orderVo.getVolume()),
                            orderVo.getPrice().subtract(new BigDecimal(daily.getClose())).divide(orderVo.getPrice(), 2, BigDecimal.ROUND_HALF_UP ).doubleValue(),
                            date ,
                            orderVo.getPrice(),
                            daily.getClose(),
                            orderVo.getVolume(),
                            JSON.toJSONString(orderVo));

                    if(date.equals(tradeConstantConfig.getToday())){ // 记录今天的交易日志
                        todayTradeLogger.info("止损 - 平空:{}, 损益:{},损益比例:{},交易日:{}, 开仓价格:{}, 平仓价格: {}, 交易量:{},  数据:{}" ,
                                orderVo.getTsCode(),
                                orderVo.getPrice().subtract(new BigDecimal(daily.getClose())).multiply(orderVo.getVolume()),
                                orderVo.getPrice().subtract(new BigDecimal(daily.getClose())).divide(orderVo.getPrice(), 2, BigDecimal.ROUND_HALF_UP ).doubleValue(),
                                date ,
                                orderVo.getPrice(),
                                daily.getClose(),
                                orderVo.getVolume(),
                                JSON.toJSONString(orderVo));
                    }

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
                    tradeService.close(orderVo, new BigDecimal(daily.getClose()), tradeConstantConfig.getUsedCapitail());
                    tradeLogger.info("止损 - 平多:{}, 损益:{},损益比例:{},交易日:{}, 开仓价格:{}, 平仓价格: {}, 交易量:{},  数据:{}" ,
                            orderVo.getTsCode(),
                            new BigDecimal(daily.getClose()).subtract(orderVo.getPrice()).multiply(orderVo.getVolume()),
                            new BigDecimal(daily.getClose()).subtract(orderVo.getPrice()).divide(orderVo.getPrice(), 2, BigDecimal.ROUND_HALF_UP ).doubleValue(),
                            date ,
                            orderVo.getPrice(),
                            daily.getClose(),
                            orderVo.getVolume(),
                            JSON.toJSONString(orderVo));

                    if(date.equals(tradeConstantConfig.getToday())){ // 记录今天的交易日志
                        todayTradeLogger.info("止损 - 平多:{}, 损益:{},损益比例:{},交易日:{}, 开仓价格:{}, 平仓价格: {}, 交易量:{},  数据:{}" ,
                                orderVo.getTsCode(),
                                new BigDecimal(daily.getClose()).subtract(orderVo.getPrice()).multiply(orderVo.getVolume()),
                                new BigDecimal(daily.getClose()).subtract(orderVo.getPrice()).divide(orderVo.getPrice(), 2, BigDecimal.ROUND_HALF_UP ).doubleValue(),
                                date ,
                                orderVo.getPrice(),
                                daily.getClose(),
                                orderVo.getVolume(),
                                JSON.toJSONString(orderVo));
                    }

                }else{
                    logger.info("止损 - 没有 多头 头寸无需止损, 交易日:{}, 数据:{}",date , JSON.toJSONString(daily));
                }
            }else{
                logger.info("止损 - 没有头寸无需止损, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
            }
        }
    }


}

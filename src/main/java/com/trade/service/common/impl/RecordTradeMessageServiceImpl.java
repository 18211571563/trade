package com.trade.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.trade.capital.CapitalManager;
import com.trade.config.TradeConstantConfig;
import com.trade.memory_storage.MemoryStorage;
import com.trade.service.common.DataService;
import com.trade.service.common.RecordTradeMessageService;
import com.trade.service.common.TradeService;
import com.trade.utils.CapitalUtil;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderBPVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 3:32
 * @DESC 记录交易信息服务
 */
@Service
public class RecordTradeMessageServiceImpl implements RecordTradeMessageService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private DataService dataService;
    @Autowired
    private CapitalManager capitalManager;

    Logger tradeLogger = LoggerFactory.getLogger("trade");
    Logger todayTradeLogger = LoggerFactory.getLogger("todayTrade");
    Logger assetLogger = LoggerFactory.getLogger("asset");
    Logger totalLogger = LoggerFactory.getLogger("total");


    /** ################################################### public ########################################################################################## **/

    /**
     * 记录开仓信息
     * @param daily
     * @param orderVo
     */
    @Override
    public void logOpen(DailyVo daily, OrderVo orderVo) {
        this.logOpen(tradeLogger, daily, orderVo);
        this.logOpen(assetLogger, daily, orderVo);
        if(daily.getTrade_date().equals(tradeConstantConfig.getToday())){ // 记录今天的交易日志
            this.logOpen(todayTradeLogger, daily, orderVo);
        }
    }

    /**
     * 记录平仓日志
     * @param daily
     * @param orderVo
     */
    @Override
    public void logClose(DailyVo daily, OrderVo orderVo) {
        this.logClose(tradeLogger, daily, orderVo);
        this.logClose(assetLogger, daily, orderVo);
        if(daily.getTrade_date().equals(tradeConstantConfig.getToday())){ // 记录今天的交易日志
            this.logClose(todayTradeLogger, daily, orderVo);
        }
    }

    /**
     * 统计
     * @param tsCode
     */
    @Override
    public synchronized void statistics(String tsCode) {
        BigDecimal sucessCount = BigDecimal.ZERO; // 胜负次数
        BigDecimal sfRate = BigDecimal.ZERO; // 胜负比例
        BigDecimal totalBp = BigDecimal.ZERO;// 总损益
        BigDecimal totalBpRate = BigDecimal.ZERO; // 总损益比例
        BigDecimal maxBpRate = BigDecimal.ZERO; // 最大收益比例
        BigDecimal minBpRate = BigDecimal.ZERO; // 最大回撤比例

        assetLogger.info("########################### {} ###############################", tsCode);
        List<OrderBPVo> orderBPVos = capitalManager.getTradeOrdersHistoryMap().get(tsCode);
        if(orderBPVos == null) return;
        for (OrderBPVo orderBPVo : orderBPVos) {
            String direction  = orderBPVo.getDirection() == 1? "多头":((orderBPVo.getDirection() == 0)? "空头":"未知") ;
            assetLogger.info("交易流水 - 方向:{}, bp:{}, bp比率:{}, 开仓价:{}, 止损价:{}, 交易量:{}, 交易时间:{} ",
                    direction, orderBPVo.getBp(), orderBPVo.getBpRate(), orderBPVo.getOpen(), orderBPVo.getClose(), orderBPVo.getVolume(), orderBPVo.getTradeDate());

            // sucessCount: 胜利次数+1
            if(orderBPVo.getBp().compareTo(BigDecimal.ZERO) > 0){
                sucessCount = sucessCount.add(BigDecimal.ONE);
            }

            // totalBp
            totalBp = totalBp.add(orderBPVo.getBp());

            // totalBpRate
            totalBpRate = totalBpRate.add(orderBPVo.getBpRate());

            // maxBpRate/minBpRate
            if(     orderBPVo.getBpRate().compareTo(BigDecimal.ZERO) > 0 &&
                    orderBPVo.getBpRate().compareTo(maxBpRate) > 0){
                maxBpRate = orderBPVo.getBpRate();
            }else if(orderBPVo.getBpRate().compareTo(BigDecimal.ZERO) < 0 &&
                    orderBPVo.getBpRate().compareTo(minBpRate) < 0){
                minBpRate = orderBPVo.getBpRate();
            }

        }

        OrderVo orderVo = tradeService.getOrderVo(tsCode);
        if(orderVo != null){
            DailyVo daily = dataService.daily(orderVo.getTsCode(), tradeConstantConfig.getStartDate(), tradeConstantConfig.getEndDate()).get(0);
            BigDecimal bp = CapitalUtil.calcBp(orderVo.getDirection(), orderVo.getPrice(), orderVo.getVolume(), daily.getClose());
            BigDecimal bp_rate = CapitalUtil.calcBpRate(orderVo.getDirection(), orderVo.getPrice(), daily.getClose());
            assetLogger.info("-");
            assetLogger.info("未平仓交易 - 方向:{}, bp:{}, bp比率:{}, 开仓价:{}, 当前价格:{},交易量:{}, 交易日: {}",
                    orderVo.getDirection() == 1? "多头":((orderVo.getDirection() == 0)? "空头":"未知"),
                    bp,
                    bp_rate,
                    orderVo.getPrice(),
                    daily.getClose(),
                    orderVo.getVolume(),
                    daily.getTrade_date());

            // sucessCount: 胜利次数+1
//            if(bp.compareTo(BigDecimal.ZERO) > 0){
//                sucessCount = sucessCount.add(BigDecimal.ONE);
//            }

            // totalBp
            totalBp = totalBp.add(bp);

            // totalBpRate
            totalBpRate = totalBpRate.add(bp_rate);

            // maxBpRate/minBpRate
            if(     bp_rate.compareTo(BigDecimal.ZERO) > 0 &&
                    bp_rate.compareTo(maxBpRate) > 0){
                maxBpRate = bp_rate;
            }else if(bp_rate.compareTo(BigDecimal.ZERO) < 0 &&
                    bp_rate.compareTo(minBpRate) < 0){
                minBpRate = bp_rate;
            }
        }

        sfRate = sucessCount.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(orderBPVos.size()), 2, BigDecimal.ROUND_HALF_UP);
        assetLogger.info("-");
        assetLogger.info("交易统计 - 总损益:{}, 总损益比例:{}, 胜率:{}%, 胜负次数:{}, 最大收益比例:{}, 最大回撤比例:{}",
                totalBp, totalBpRate , sfRate, sucessCount, maxBpRate, minBpRate);

        assetLogger.info("----------------------------------------------------------------------------------------");
        assetLogger.info(" ");
    }

    /**
     * 资金信息
     */
    @Override
    public void statisticsCapital(){
        assetLogger.info("########################### {} ###############################", "资金信息");
        assetLogger.info("资金信息 - 总资金:{}, 可用资金:{}, 冻结资金: {}, 风险系数:{}, 交易失败次数:{}",capitalManager.getTotalCapital(), capitalManager.getUsableCapital(), capitalManager.getFrozenCapital(), capitalManager.getRiskParameter(), capitalManager.getFailedTradeCount());
        assetLogger.info("------------------------------------------ {}% ----------------------------------------------",
                capitalManager.getTotalCapital().subtract(BigDecimal.valueOf(tradeConstantConfig.getTotalCapital())).multiply(new BigDecimal(100)).divide(BigDecimal.valueOf(tradeConstantConfig.getTotalCapital()), 4, BigDecimal.ROUND_HALF_UP));

        totalLogger.info("########################### {} ###############################", MDC.get("traceId"));
        totalLogger.info("配置信息：{}", JSON.toJSONString(tradeConstantConfig));
        totalLogger.info("资金信息 - 总资金:{}, 可用资金:{}, 冻结资金: {}, 风险系数:{}, 交易失败次数:{}",capitalManager.getTotalCapital(), capitalManager.getUsableCapital(), capitalManager.getFrozenCapital(), capitalManager.getRiskParameter(), capitalManager.getFailedTradeCount());
        totalLogger.info("------------------------------------------ {}% ----------------------------------------------",
                capitalManager.getTotalCapital().subtract(BigDecimal.valueOf(tradeConstantConfig.getTotalCapital())).multiply(new BigDecimal(100)).divide(BigDecimal.valueOf(tradeConstantConfig.getTotalCapital()), 4, BigDecimal.ROUND_HALF_UP));
        totalLogger.info(System.lineSeparator());
    }

    /**
     * 打印交易中的资金信息
     */
    @Override
    public void simpleStatisticsCapital(BigDecimal frozenCapital){
        tradeLogger.info("资金信息 - 标的操作金额:{}, 总资金:{}, 可用资金:{}, 冻结资金: {}, 风险系数:{}"+ System.lineSeparator(), frozenCapital, capitalManager.getTotalCapital(), capitalManager.getUsableCapital(), capitalManager.getFrozenCapital(), capitalManager.getRiskParameter() );
    }

    /** ################################################### private ########################################################################################## **/



    /**
     * 格式化开仓日志格式
     * @param logger
     * @param daily
     * @param tradeOrderVo
     */
    private void logOpen(Logger logger, DailyVo daily, OrderVo tradeOrderVo) {
        String direction = "未知";
        if(tradeOrderVo.getDirection() == 0){
            direction = "空头";
        }else if(tradeOrderVo.getDirection() == 1){
            direction = "多头";
        }

        logger.info("交易 - 标的:{}, 方向:{}, 价格:{}, 交易量:{}, 交易日:{}",
                tradeOrderVo.getTsCode(),
                direction,
                tradeOrderVo.getPrice(),
                tradeOrderVo.getVolume(),
                daily.getTrade_date());
    }

    /**
     * 格式化平仓日志格式
     * @param daily
     * @param orderVo
     * @param logger
     */
    private void logClose(Logger logger, DailyVo daily, OrderVo orderVo) {
        String direction = "未知";

        if(orderVo.getDirection() == 0){
            direction = "平空";
        }else if(orderVo.getDirection() == 1){
            direction = "平多";
        }

        logger.info("止损 - 标的:{}, 方向:{}, 损益金额:{},损益比例:{},交易日:{}, 开仓价格:{}, 平仓价格: {}, 交易量:{}",
                orderVo.getTsCode(),
                direction,
                CapitalUtil.calcBp(daily, orderVo).doubleValue(),
                CapitalUtil.calcBpRate(daily, orderVo).doubleValue(),
                daily.getTrade_date(),
                orderVo.getPrice(),
                daily.getClose(),
                orderVo.getVolume());
    }

}

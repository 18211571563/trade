package com.trade.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.trade.capital.CapitalManager;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.RecordTradeMessageService;
import com.trade.utils.CapitalUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderBPVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RecordTradeServiceMessageImpl implements RecordTradeMessageService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;

    Logger tradeLogger = LoggerFactory.getLogger("trade");
    Logger todayTradeLogger = LoggerFactory.getLogger("todayTrade");
    Logger assetLogger = LoggerFactory.getLogger("asset");


    /** ################################################### public ########################################################################################## **/

    /**
     * 记录开仓信息
     * @param daily
     * @param orderVo
     */
    @Override
    public void logOpen(DailyVo daily, OrderVo orderVo) {
        this.logOpen(tradeLogger, daily, orderVo);
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
        if(daily.getTrade_date().equals(tradeConstantConfig.getToday())){ // 记录今天的交易日志
            this.logClose(todayTradeLogger, daily, orderVo);
        }
        this.saveTradeOrdersHistory(daily, orderVo);
    }

    /**
     * 统计
     * @param tsCode
     */
    @Override
    public void statistics(String tsCode) {
        BigDecimal sucessCount = BigDecimal.ZERO; // 胜负次数
        BigDecimal sfRate = BigDecimal.ZERO; // 胜负比例
        BigDecimal totalBp = BigDecimal.ZERO;// 总损益
        BigDecimal totalBpRate = BigDecimal.ZERO; // 总损益比例
        BigDecimal maxBpRate = BigDecimal.ZERO; // 最大收益比例
        BigDecimal minBpRate = BigDecimal.ZERO; // 最大回撤比例

        assetLogger.info("########################### {} ###############################", tsCode);
        List<OrderBPVo> orderBPVos = CapitalManager.tradeOrdersHistoryMap.get(tsCode);
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
        sfRate = sucessCount.divide(BigDecimal.valueOf(orderBPVos.size()), 2, BigDecimal.ROUND_HALF_UP);
        assetLogger.info("交易统计 - 总损益:{}, 总损益比例:{}, 胜负次数:{}, 胜负比例:{}, 最大收益比例:{}, 最大回撤比例:{}",
                totalBp, totalBpRate, sucessCount, sfRate, maxBpRate, minBpRate);
        assetLogger.info("------------------------------------------ {} ----------------------------------------------", "END");
        assetLogger.info(" ");
    }

    /**
     * 资金信息
     */
    @Override
    public void statisticsCapital(){
        assetLogger.info("########################### {} ###############################", "资金信息");
        assetLogger.info("资金信息 - 总资金:{}, 可用资金:{}, 冻结资金: {}, 风险系数:{}",CapitalManager.assetVo.getTotalCapital(), CapitalManager.assetVo.getUsableCapital(), CapitalManager.assetVo.getFrozenCapital(), CapitalManager.assetVo.getRiskParameter());
        assetLogger.info("------------------------------------------ {} ----------------------------------------------", "END");
    }

    /** ################################################### private ########################################################################################## **/

    /**
     * 保存交易订单历史记录
     * @param daily
     * @param orderVo
     */
    private void saveTradeOrdersHistory(DailyVo daily, OrderVo orderVo) {
        List<OrderBPVo> orderBPVos = CapitalManager.tradeOrdersHistoryMap.get(daily.getTs_code());
        if(orderBPVos == null){
            orderBPVos = new ArrayList<>();
            CapitalManager.tradeOrdersHistoryMap.put(daily.getTs_code(), orderBPVos);
        }
        OrderBPVo orderBPVo = new OrderBPVo(orderVo.getTsCode(), orderVo.getDirection(),CapitalUtil.calcBp(daily, orderVo),CapitalUtil.calcBpRate(daily, orderVo),daily.getTrade_date(), orderVo.getPrice(),new BigDecimal(daily.getClose()),orderVo.getVolume());
        orderBPVos.add(orderBPVo);

    }

    /**
     * 格式化平仓日志格式
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

        logger.info("交易 - 标的:{}, 方向:{}, 价格:{}, 交易量:{}, 交易日:{}, 数据:{}",
                tradeOrderVo.getTsCode(),
                direction,
                tradeOrderVo.getPrice(),
                tradeOrderVo.getVolume(),
                daily.getTrade_date(),
                JSON.toJSONString(tradeOrderVo));
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

        logger.info("止损 - 标的:{}, 方向:{}, 损益:{},损益比例:{},交易日:{}, 开仓价格:{}, 平仓价格: {}, 交易量:{},  数据:{}",
                orderVo.getTsCode(),
                direction,
                CapitalUtil.calcBp(daily, orderVo).doubleValue(),
                CapitalUtil.calcBpRate(daily, orderVo).doubleValue(),
                daily.getTrade_date(),
                orderVo.getPrice(),
                daily.getClose(),
                orderVo.getVolume(),
                JSON.toJSONString(orderVo));
    }

}

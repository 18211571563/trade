package com.trade.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.trade.capital.CapitalManager;
import com.trade.config.StrategyConstantConfig;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.RecordTradeMessageService;
import com.trade.service.common.TradeService;
import com.trade.utils.CapitalUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-14 下午 2:40
 * @DESC 交易服务
 */
@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private RecordTradeMessageService recordTradeMessageService;
    @Autowired
    private StrategyConstantConfig strategyConstantConfig;
    @Autowired
    private CapitalManager capitalManager;

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger assetLogger = LoggerFactory.getLogger("asset");
    Logger tradeLogger = LoggerFactory.getLogger("trade");

    @Override
    public synchronized void open(DailyVo daily, OrderVo orderVo) {

        /** 资金控制 - 判断现在的可用资金是否满足订单金额 并且 冻结金额 **/
        if(BigDecimal.ZERO.compareTo(orderVo.getVolume()) == 0){
            tradeLogger.error("交易量不可为零，标的:{}，数量:{} ", orderVo.getTsCode(), orderVo.getVolume());
            assetLogger.error("交易量不可为零，标的:{}，数量:{} ", orderVo.getTsCode(), orderVo.getVolume());
            capitalManager.addFailedTradeCount();
            return;
        }
        if(capitalManager.getUsableCapital().compareTo(orderVo.getPrice().multiply(orderVo.getVolume())) < 0){
            tradeLogger.error("可用金额不足，可用金额:{}, 订单金额:{}", capitalManager.getUsableCapital(), orderVo.getPrice().multiply(orderVo.getVolume()));
            assetLogger.error("可用金额不足，可用金额:{}, 订单金额:{}", capitalManager.getUsableCapital(), orderVo.getPrice().multiply(orderVo.getVolume()));
            capitalManager.addFailedTradeCount();
            return;
        }

        /** 开仓 - 保存订单 **/
        BigDecimal tsCapital = orderVo.getPrice().multiply(orderVo.getVolume());

        capitalManager.openCapital(tsCapital);
        capitalManager.addTradeOrders(daily, orderVo);

        /** 记录交易日志 **/
        recordTradeMessageService.logOpen(daily, orderVo);
        /** 打印资金信息 **/
        recordTradeMessageService.simpleStatisticsCapital(tsCapital);
    }


    @Override
    public synchronized void close(DailyVo daily, OrderVo orderVo) {


        /** 核算资金 **/
        // 计算盈亏
        BigDecimal bp = CapitalUtil.calcBp(daily, orderVo);

        // 标的资产 = 投入金额 + 盈亏
        BigDecimal tsCapital = orderVo.getPrice().multiply(orderVo.getVolume()).add(bp);

        capitalManager.closeCapital(tsCapital, bp);
        capitalManager.removeTradeOrders(daily, orderVo);

        /** 记录交易日志 **/
        recordTradeMessageService.logClose(daily, orderVo);
        /** 打印资金信息 **/
        recordTradeMessageService.simpleStatisticsCapital(tsCapital);
    }


    /**
     * 获取交易池中的订单
     * @param tsCode
     * @return
     */
    @Override
    public OrderVo getOrderVo(String tsCode){
        return capitalManager.getOrderVo(tsCode);
    }

    /**
     * 判断是否持仓
     * @param orderVo
     * @return
     */
    @Override
    public Boolean isHoldPosition(OrderVo orderVo) {
        return orderVo != null;
    }

    /**
     * 是否容许开仓
     * @param daily
     * @param orderVo
     * @return
     */
    @Override
    public Boolean allowOpen(DailyVo daily, OrderVo orderVo) {
        Boolean allow = true;
        // 判断是否持仓
        Boolean isHoldPosition = this.isHoldPosition(orderVo);
        if(isHoldPosition){ // 持有仓位
            logger.info("已经存在仓位无需交易, 交易日:{}, 数据:{}" ,daily.getTrade_date() , JSON.toJSONString(daily));
            allow = false;
        }
        return allow;
    }

    /**
     * 是否容许止损
     * @param daily
     * @param orderVo
     * @return
     */
    @Override
    public Boolean allowClose(DailyVo daily, OrderVo orderVo) {
        Boolean allow = true;
        // 判断是否持仓
        Boolean isHoldPosition = this.isHoldPosition(orderVo);
        if(!isHoldPosition){ // 没有持有仓位
            logger.info("止损 - 没有头寸无需止损, 交易日:{}," ,daily.getTrade_date() , JSON.toJSONString(daily));
            allow = false;
        }
        return allow;
    }


    /**
     * 选择开仓策略编码
     * @param strategy
     * @return
     */
    @Override
    public String selectOpenStrategy(String strategy) {
        String s = strategyConstantConfig.getOpen_codes().get(strategy);
        if(s != null) return s;
        return "";
    }

    /**
     * 选择止损策略编码
     * @param strategy
     * @return
     */
    @Override
    public String selectCloseStrategy(String strategy) {
        String s = strategyConstantConfig.getClose_codes().get(strategy);
        if(s != null) return s;
        return "";
    }


}

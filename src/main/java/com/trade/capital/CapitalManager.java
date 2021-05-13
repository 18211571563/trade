package com.trade.capital;

import com.trade.config.TradeConstantConfig;
import com.trade.utils.CapitalUtil;
import com.trade.vo.AssetVo;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderBPVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by georgy on 2020-02-24.
 * 资产管理 - 资金变动主要有2种，记录资金日志的变化主要从这2方面入手
 *  1： 盈亏，造成资金总数的变动
 *  2： 冻结与解冻，不影响资金总数
 */
@Component
public class CapitalManager {

    private Logger capitalLogger = LoggerFactory.getLogger("capital");

    private static Lock lockCapital = new ReentrantLock();
    public static Lock lockOrderVo = new ReentrantLock();

    @Autowired
    private TradeConstantConfig tradeConstantConfig;

    /** 交易订单 **/
    private static List<OrderVo> tradeOrders;

    /** 资金池 **/
    private static AssetVo assetVo;

    /** 订单历史记录 **/
    private static Map<String, List<OrderBPVo>> tradeOrdersHistoryMap;

    /** 交易失败次数 **/
    private static int failedTradeCount = 0;

    public void init(){
        tradeOrders = Collections.synchronizedList(new ArrayList<>());
        assetVo = AssetVo.create(   BigDecimal.valueOf(tradeConstantConfig.getTotalCapital()),
                                    BigDecimal.valueOf(tradeConstantConfig.getRiskParameter()));
        tradeOrdersHistoryMap = new Hashtable<>();
        failedTradeCount = 0;
    }

    /**
     * 冻结金额操作(开仓)
     * @param capital 资金
     */
    public synchronized void openCapital(BigDecimal capital){
        lockCapital.lock();
        try {
            this.doFrozenCapital(capital);
        }finally {
            lockCapital.unlock();
        }

    }

    /**
     * 计算盈亏，解冻资金(平仓)
     * @param capital 解冻资金
     * @param bp 盈亏
     */
    public synchronized void closeCapital(BigDecimal capital, BigDecimal bp){
        lockCapital.lock();
        try {
            this.calCapitalByBP(bp);
            this.doFrozenCapital(capital.negate());
        }finally {
            lockCapital.unlock();
        }

    }

    /**
     * 根据盈亏计算资金信息
     * @return
     */
    private synchronized void calCapitalByBP(BigDecimal bp){
        BigDecimal originalTotalCapital = this.getTotalCapital();
        BigDecimal originalUsableCapital = this.getUsableCapital();
        BigDecimal originalFrozenCapital = this.getFrozenCapital();

        CapitalManager.assetVo.setFrozenCapital(CapitalManager.assetVo.getFrozenCapital().add(bp));
        CapitalManager.assetVo.setTotalCapital(CapitalManager.assetVo.getTotalCapital().add(bp));

        capitalLogger.info("总资金:{}, 盈亏:{}, 可用资金:{}, 冻结资金:{}, 初始总资金:{}, 初始可用资金:{}, 初始冻结资金:{}",
                this.getTotalCapital(),
                bp,
                this.getUsableCapital(),
                this.getFrozenCapital(),
                originalTotalCapital,
                originalUsableCapital,
                originalFrozenCapital
        );

    }

    /**
     * 冻结金额操作 - 正数冻结，负数释放
     */
    private synchronized void doFrozenCapital(BigDecimal capital){
        BigDecimal originalTotalCapital = this.getTotalCapital();
        BigDecimal originalUsableCapital = this.getUsableCapital();
        BigDecimal originalFrozenCapital = this.getFrozenCapital();

        CapitalManager.assetVo.setFrozenCapital(CapitalManager.assetVo.getFrozenCapital().add(capital));

        capitalLogger.info("总资金:{}, 可用资金:{}, 冻结资金:{}, 初始总资金:{}, 初始可用资金:{}, 初始冻结资金:{}, 需冻结资金:{}",
                this.getTotalCapital(),
                this.getUsableCapital(),
                this.getFrozenCapital(),
                originalTotalCapital,
                originalUsableCapital,
                originalFrozenCapital,
                capital
        );
    }

    /**
     * 获取总资金
     * @return
     */
    public BigDecimal getTotalCapital(){
        return assetVo.getTotalCapital();
    }

    /**
     * 获取冻结资金
     * @return
     */
    public BigDecimal getFrozenCapital(){
        return assetVo.getFrozenCapital();
    }

    /**
     * 获取可用资金
     * @return
     */
    public BigDecimal getUsableCapital(){
        return this.getTotalCapital().subtract(this.getFrozenCapital());
    }

    public BigDecimal getRiskParameter() {
        return assetVo.getRiskParameter();
    }

    /**
     * 订单历史记录
     * @return
     */
    public Map<String, List<OrderBPVo>> getTradeOrdersHistoryMap(){
        return tradeOrdersHistoryMap;
    }

    /**
     * 保存订单历史记录
     * @param daily
     * @param orderVo
     */
    public void saveTradeOrdersHistory(DailyVo daily, OrderVo orderVo) {
        List<OrderBPVo> orderBPVos = CapitalManager.tradeOrdersHistoryMap.get(daily.getTs_code());
        if(orderBPVos == null){
            orderBPVos = new ArrayList<>();
            CapitalManager.tradeOrdersHistoryMap.put(daily.getTs_code(), orderBPVos);
        }
        OrderBPVo orderBPVo = new OrderBPVo(orderVo.getTsCode(), orderVo.getDirection(), CapitalUtil.calcBp(daily, orderVo),CapitalUtil.calcBpRate(daily, orderVo),daily.getTrade_date(), orderVo.getPrice(),new BigDecimal(daily.getClose()),orderVo.getVolume());
        orderBPVos.add(orderBPVo);

    }

    /**
     * 获取交易订单
     * @return
     */
    public List<OrderVo> getTradeOrders(){
        return CapitalManager.tradeOrders;
    }

    /**
     * 获取交易订单单个
     * @param tsCode
     * @return
     */
    public OrderVo getOrderVo(String tsCode){
        lockOrderVo.lock();
        try {
            if(!CollectionUtils.isEmpty(this.getTradeOrders())){
                for (OrderVo orderVo : this.getTradeOrders()) {
                    if(orderVo.getTsCode().equals(tsCode)){
                        return orderVo;
                    }
                }
            }
        }finally {
            lockOrderVo.unlock();
        }

        return null;
    }

    /**
     * 添加交易订单(开仓)
     * @param orderVo
     */
    public void addTradeOrders(DailyVo daily, OrderVo orderVo){
        lockOrderVo.lock();
        try {
            CapitalManager.tradeOrders.add(orderVo);
        }finally {
            lockOrderVo.unlock();
        }
    }

    /**
     * 移除交易订单(平仓)
     * @param orderVo
     */
    public void removeTradeOrders(DailyVo daily, OrderVo orderVo){
        lockOrderVo.lock();
        try {
            // 平仓交易
            CapitalManager.tradeOrders.remove(orderVo);
            // 平仓后记录交易历史
            this.saveTradeOrdersHistory(daily, orderVo);
        }finally {
            lockOrderVo.unlock();
        }
    }

    /**
     * 获取交易失败次数
     * @return
     */
    public int getFailedTradeCount() {
        return this.failedTradeCount;
    }

    /**
     * 添加交易失败次数记录
     * @return
     */
    public synchronized void addFailedTradeCount() {
        this.failedTradeCount = this.getFailedTradeCount() + 1;
    }

}

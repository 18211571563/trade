package com.trade.service.strategy.open.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.StrategyConstantConfig;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.CalculateService;
import com.trade.service.common.DataService;
import com.trade.service.strategy.open.BearOpenStrategyService;
import com.trade.service.strategy.open.BullOpenStrategyService;
import com.trade.service.strategy.open.OpenStrategyService;
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
 * @Date 2020-03-30 上午 11:20
 * @DESC 开仓策略
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
    @Autowired
    private BearOpenStrategyService bearOpenStrategyService;
    @Autowired
    private BullOpenStrategyService bullOpenStrategyService;


    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 开仓策略
     * @param daily
     * @param orderVo
     */
    @Override
    public void open( DailyVo daily, OrderVo orderVo, String openStrategyCode) {
        /***************************************************************** 是否容许开仓 ************************************************************************/
        if(!tradeService.allowOpen(daily, orderVo)) return;

        /***************************************************************** 获取过滤线趋势 ************************************************************************/
        BigDecimal filterTrend = calculateService.getFilterTrend(daily.getTs_code(), daily.getTrade_date(), tradeConstantConfig.getFilterDay());

        /***************************************************************** 开仓策略逻辑 ************************************************************************/
        if(tradeService.selectOpenStrategy("breakOpen").equals(openStrategyCode)){
            this.breakOpen(daily, filterTrend);
        }else{
            throw new RuntimeException("没有可用的开仓策略");
        }


    }

    /**
     * 突破开仓策略
     * @param daily
     * @param filterTrend
     */
    private void breakOpen(DailyVo daily, BigDecimal filterTrend) {
        // 计算突破点
        List<DailyVo> breakOpenDailyVo = dataService.daily(daily.getTs_code(), daily.getTrade_date(), tradeConstantConfig.getBreakOpenDay());
        DailyVo maxOpen = CapitalUtil.getMax(breakOpenDailyVo);
        DailyVo minOpen = CapitalUtil.getMin(breakOpenDailyVo);

        if(filterTrend.compareTo(BigDecimal.ZERO) > 0){
            logger.info("过滤线方向大于零,运行多头策略!");
            bullOpenStrategyService.bullBreakOpen(daily, maxOpen);

        }else if(filterTrend.compareTo(BigDecimal.ZERO) < 0){
            logger.info("过滤线方向小于零,运行空头策略!");
            bearOpenStrategyService.bearBreakOpen(daily, minOpen);

        }else if(filterTrend.compareTo(BigDecimal.ZERO) == 0) {
            logger.info("过滤线无方向(0),不进行开仓!");
            return;
        }
    }

    /**
     * 突破波动率(R)开仓策略
     * @param daily
     * @param orderVo
     */
    private void breakROpen(DailyVo daily, OrderVo orderVo) {
        String tsCode = daily.getTs_code();
        String date = daily.getTrade_date();



        // 判断是否持仓
        Boolean isHoldPosition = tradeService.isHoldPosition(orderVo);
        if(!isHoldPosition) { // 空仓

        }else{
            logger.info("已经存在仓位无需交易, 交易日:{}, 数据:{}" ,date , JSON.toJSONString(daily));
        }

    }

}

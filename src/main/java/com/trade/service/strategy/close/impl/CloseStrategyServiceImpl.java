package com.trade.service.strategy.close.impl;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.strategy.close.BearCloseStrategyService;
import com.trade.service.strategy.close.BullCloseStrategyService;
import com.trade.service.strategy.close.CloseStrategyService;
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
    private BearCloseStrategyService bearCloseStrategyService;
    @Autowired
    private BullCloseStrategyService bullCloseStrategyService;

    Logger logger = LoggerFactory.getLogger(getClass());


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

        if(orderVo != null && orderVo.getDirection() == 0){
            bearCloseStrategyService.bearBreakClose(daily, maxClose, orderVo);

        }else if(orderVo != null && orderVo.getDirection() == 1){
            bullCloseStrategyService.bullBreakClose(daily, minClose, orderVo);

        }else {
            logger.info("止损 - 没有头寸无需止损, 交易日:{}, 数据:{}" ,daily.getTrade_date() , JSON.toJSONString(daily));

        }

    }

}

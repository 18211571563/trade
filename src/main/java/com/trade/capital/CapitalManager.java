package com.trade.capital;

import com.trade.config.TradeConstantConfig;
import com.trade.vo.AssetVo;
import com.trade.vo.OrderBPVo;
import com.trade.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by georgy on 2020-02-24.
 * 资金管理
 */
@Component
public class CapitalManager {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;

    /** 交易订单记录 **/
    public static List<OrderVo> tradeOrders;

    /** 资金池 **/
    public static AssetVo assetVo;

    /** 保存交易订单历史记录 **/
    public static Map<String, List<OrderBPVo>> tradeOrdersHistoryMap;

    public void init(){
        tradeOrders = Collections.synchronizedList(new ArrayList<>());
        assetVo = AssetVo.create(   BigDecimal.valueOf(tradeConstantConfig.getTotalCapital()),
                                    BigDecimal.valueOf(tradeConstantConfig.getRiskParameter()));
        tradeOrdersHistoryMap = new Hashtable<>();
    }



}

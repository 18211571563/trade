package com.trade.service;

import com.trade.vo.AssetVo;
import com.trade.vo.OrderVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-14 下午 2:38
 * @DESC TODO
 */
public interface TradeService {

    void open(OrderVo orderVo, boolean isUsedCapitail);
    void close(OrderVo orderVo, BigDecimal closePrice, boolean isUsedCapitail);
    OrderVo getOrderVo(String tsCode);

    BigDecimal getTotalCapital();

    void calTotalCapital(BigDecimal bp);

    BigDecimal getRiskParameter();

    List<OrderVo> getTradeOrders();
}

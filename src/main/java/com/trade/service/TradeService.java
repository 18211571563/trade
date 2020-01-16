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

    public static List<OrderVo> tradeOrders = new ArrayList<>();
    public static AssetVo assetVo = new AssetVo(BigDecimal.valueOf(300000), BigDecimal.valueOf(50));
    public static int atrPeriod = 30;
    public static int breakOpenDay = 50;
    public static int breakCloseDay = 25;
    public static int filterDay = 200;

    void open(OrderVo orderVo);
    void close(OrderVo orderVo, BigDecimal closePrice);
    OrderVo getOrderVo(String tsCode);

    BigDecimal getTotalCapital();

    void calTotalCapital(BigDecimal bp);

    BigDecimal getRiskParameter();

    List<OrderVo> getTradeOrders();
}

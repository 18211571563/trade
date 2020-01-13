package com.trade.service;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-13 下午 3:03
 * @DESC TODO
 */
public interface CalculateService {
    List<BigDecimal> getDailyAtrs(String tsCode, List<DailyVo> dailys, String date, int atrPeriod);

    OrderVo getOrder(List<OrderVo> tradeOrder, String tsCode);
}

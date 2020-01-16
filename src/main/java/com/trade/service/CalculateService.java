package com.trade.service;

import com.trade.vo.OrderVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-13 下午 3:03
 * @DESC TODO
 */
public interface CalculateService {
    BigDecimal getDailyAverageAtr(String tsCode, String date, int atrPeriod);

    BigDecimal getFilterTrend(String tsCode, String startDate, int day);
}

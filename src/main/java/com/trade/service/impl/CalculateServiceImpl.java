package com.trade.service.impl;

import com.trade.service.CalculateService;
import com.trade.service.DataService;
import com.trade.service.TradeService;
import com.trade.utils.CapitalUtil;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-13 下午 3:04
 * @DESC TODO
 */
@Service
public class CalculateServiceImpl implements CalculateService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataService dataService;


    /**
     * 获取今日 atr
     * @param tsCode
     * @param date
     * @param atrPeriod
     * @return
     */
    @Override
    public BigDecimal getDailyAverageAtr(String tsCode, String date, int atrPeriod) {
        // 获取计算ATR所需的前置 atrPeriod 天数据
        List<DailyVo> getCalculateAtrDailyVos = this.getCalculateAtrDailyVos(tsCode, date, atrPeriod); // 有 atrPeriod  + 1 条数据
        // 计算今日 ATR
        BigDecimal dailyAtr = CapitalUtil.getDailyAtr(getCalculateAtrDailyVos);
        return dailyAtr;
    }

    /**
     * 获取计算Atr的数据
     * @param tsCode
     * @param startDate
     * @return
     */
    private List<DailyVo> getCalculateAtrDailyVos(String tsCode, String startDate, int atrPeriod) {
        // 获取前 atrPeriod 天的数据，用来计算atr
        LocalDate startDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER).minus(atrPeriod , ChronoUnit.DAYS  );
        LocalDate endDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER);
        return dataService.daily(tsCode, startDateL.format(TimeUtil.SHORT_DATE_FORMATTER), endDateL.format(TimeUtil.SHORT_DATE_FORMATTER));
    }

    /**
     * 获取 过滤器 趋势
     * @param tsCode
     * @param day
     * @return
     */
    @Override
    public BigDecimal getFilterTrend(String tsCode, String startDate, int day) {
        // 获取最近200天数据
        LocalDate startDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER).minus(day - 1  , ChronoUnit.DAYS  );
        LocalDate endDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER);
        List<DailyVo> dailys = dataService.daily(tsCode, startDateL.format(TimeUtil.SHORT_DATE_FORMATTER), endDateL.format(TimeUtil.SHORT_DATE_FORMATTER));

        // 获取价格 时间序列 数据
        List<BigDecimal> closes = new ArrayList<>();
        dailys.forEach(dailyVo -> {closes.add(new BigDecimal(dailyVo.getClose()));});
        return this.trend(closes);
    }

    /**
     * 时间序列数据 自定义趋势算法
     * @param closes
     * @return
     */
    private BigDecimal trend(List<BigDecimal> closes) {
        // 平均值
        double average = CapitalUtil.average(closes);

        // 开始值，结束值
        BigDecimal begin = closes.get(0);
        BigDecimal end = closes.get(closes.size() - 1);

        // 获取最大值与最小值及其坐标
        BigDecimal min = closes.get(0);
        BigDecimal max = closes.get(0);
        int minIndex = 0;
        int maxIndex = 0;
        for (int i = 0; i < closes.size(); i++) {
            BigDecimal close = closes.get(i);
            if(close.compareTo(min) < 0){
                min = close;
                minIndex = i;
            }
            if(close.compareTo(max) > 0){
                max = close;
                maxIndex = i;
            }
        }

        // 简单计算趋势
        BigDecimal b = end.subtract(begin).divide(begin,8, BigDecimal.ROUND_HALF_UP); // (e-b)/b
        BigDecimal eb = b.divide(BigDecimal.valueOf(closes.size()),  8, BigDecimal.ROUND_HALF_UP);

        return eb.add(TradeService.marketTrendOffset);
    }


}

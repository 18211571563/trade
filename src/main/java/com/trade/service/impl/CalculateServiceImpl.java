package com.trade.service.impl;

import com.trade.service.CalculateService;
import com.trade.service.DataService;
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



}

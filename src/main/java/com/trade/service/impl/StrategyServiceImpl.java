package com.trade.service.impl;

import com.trade.service.DataService;
import com.trade.service.StrategyService;
import com.trade.utils.CapitalUtil;
import com.trade.vo.DailyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author georgy
 * @Date 2020-01-09 下午 4:32
 * @DESC TODO
 */
@Service
public class StrategyServiceImpl implements StrategyService {

    public static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");


    @Autowired
    private DataService dataService;

    private Map<String, String>  assetMap; // 资产
    private BigDecimal totalCapital = BigDecimal.valueOf(100000.00); // 总资金
    private BigDecimal riskParameter; // 风险系数
    private String startDate = "20190101";
    private String endDate = "20191231";
    private int atrPeriod = 30;

    @Override
    public void process(){
        /************************** 一，计算atr ******************************/
        // 获取数据
        List<DailyVo> datas = this.getData("000100.SZ", startDate, endDate);

        // 第N个交易日, 0开始
        int day = 0;
        BigDecimal dailyAtr = getDailyAtr(datas, day);
    }

    /**
     * 获取每日atr
     * @param datas 数据集合
     * @param day 第几日， 0 开始
     * @return
     */
    private BigDecimal getDailyAtr(List<DailyVo> datas, int day) {

        List<DailyVo> dailyVos = datas.subList(datas.size() - atrPeriod - 1 - day, datas.size() - day); // 有 atrPeriod  + 1 条数据
        List<BigDecimal> highs = new ArrayList<>();
        List<BigDecimal> lows = new ArrayList<>();
        List<BigDecimal> closes = new ArrayList<>();

        for (int i = dailyVos.size() - 1; i >= 0; i--) {
            if(i == (dailyVos.size() - 1) ) continue;
            DailyVo yesterdayDailyVo = dailyVos.get(i + 1); // 昨天
            DailyVo todayDailyVo = dailyVos.get(i);         // 今天

            highs.add(new BigDecimal(todayDailyVo.getHigh()));
            lows.add(new BigDecimal(todayDailyVo.getLow()));
            closes.add(new BigDecimal(yesterdayDailyVo.getClose()));
        }

        BigDecimal atr = CapitalUtil.atr(highs, lows, closes, atrPeriod);
        return atr;
    }


    /**
     * 获取数据  - 获取 开始时间 - atr周期 到 结束时间的所有数据
     */
    private List<DailyVo> getData(String tsCode, String startDate, String endDate){
        List<DailyVo> datas = new ArrayList<>();
        // 获取前 atrPeriod 天的数据，用来计算atr
        LocalDate startDateL = LocalDate.parse(startDate, SHORT_DATE_FORMATTER).minus(atrPeriod * 2, ChronoUnit.DAYS );
        LocalDate endDateL = LocalDate.parse(startDate, SHORT_DATE_FORMATTER).minus(1, ChronoUnit.DAYS );
        List<DailyVo> atrPeriodDailyList = dataService.daily(tsCode, startDateL.format(SHORT_DATE_FORMATTER), endDateL.format(SHORT_DATE_FORMATTER));

        // 获取常规数据
        List<DailyVo> dailys = dataService.daily(tsCode, startDate, endDate);

        // 组织数据
        datas.addAll(dailys);
        datas.addAll(atrPeriodDailyList.subList(0 , atrPeriod)); // 获取第atrPeriod的子集合
        return datas;
    }


}



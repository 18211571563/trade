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
     * 获取数据的ATR集合
     * @param dailys
     */
    @Override
    public List<BigDecimal> getDailyAtrs(String tsCode, List<DailyVo> dailys, String date, int atrPeriod) {
        // 获取计算ATR所需的前置 atrPeriod 条数据
        List<DailyVo> getCalculateAtrDailyVos = this.getCalculateAtrDailyVos(tsCode, date, atrPeriod);
        // 合并集合
        List<BigDecimal> dailyAtrs = new ArrayList();
        List<DailyVo> datas = new ArrayList<>();
        datas.addAll(getCalculateAtrDailyVos);
        datas.addAll(dailys);

        for(int day = 0; day < dailys.size(); day++ ){
            // 第N个交易日 atr, 0开始
            BigDecimal dailyAtr = CapitalUtil.getDailyAtr(datas, day, atrPeriod);
            dailyAtrs.add(dailyAtr);
        }
        return dailyAtrs;
    }

    /**
     * 获取计算Atr的数据
     * @param tsCode
     * @param startDate
     * @return
     */
    private List<DailyVo> getCalculateAtrDailyVos(String tsCode, String startDate, int atrPeriod) {
        // 获取前 atrPeriod 天的数据，用来计算atr
        LocalDate startDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER).minus(atrPeriod * 2, ChronoUnit.DAYS );
        LocalDate endDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER).minus(1, ChronoUnit.DAYS );
        return dataService.daily(tsCode, startDateL.format(TimeUtil.SHORT_DATE_FORMATTER), endDateL.format(TimeUtil.SHORT_DATE_FORMATTER));
    }

    @Override
    public OrderVo getOrder(List<OrderVo> tradeOrder, String tsCode) {
        if(!CollectionUtils.isEmpty(tradeOrder)){
            for (OrderVo orderVo : tradeOrder) {
                if(orderVo.getTsCode().equals(tsCode)){
                    return orderVo;
                }
            }
        }
        return null;
    }





}

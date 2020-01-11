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
        String tsCode = "000100.SZ";
        /***************************************************************** 一，准备基础数据 *********************************************************************/

        // 获取 startDate -> endDate 数据
        List<DailyVo> dailys = dataService.daily(tsCode, startDate, endDate);


        /***************************************************************** 二，计算atr *********************************************************************/
        List<BigDecimal> dailyAtrs = this.getDailyAtrs(tsCode, dailys, startDate, atrPeriod);


        /***************************************************************** 三，开仓 ************************************************************************/
        // 根据当前信息判断是否有开仓信号

        // 获取滤镜，判断当前的开仓信号是否与长期趋势背离，如背离，终止交易

        // 如开仓，根据atr计算 下注手数


        /***************************************************************** 四，止损 ************************************************************************/
        // 根据当前信息判断是否有止损信号

        // 如果止损并且持有仓位，进行平仓操作





    }


    /**
     * 获取数据的ATR集合
     * @param dailys
     */
    private List<BigDecimal> getDailyAtrs(String tsCode, List<DailyVo> dailys, String startDate, int atrPeriod) {
        // 获取计算ATR所需的前置 atrPeriod 条数据
        List<DailyVo> getCalculateAtrDailyVos = this.getCalculateAtrDailyVos(tsCode, startDate, atrPeriod);
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
        LocalDate startDateL = LocalDate.parse(startDate, SHORT_DATE_FORMATTER).minus(atrPeriod * 2, ChronoUnit.DAYS );
        LocalDate endDateL = LocalDate.parse(startDate, SHORT_DATE_FORMATTER).minus(1, ChronoUnit.DAYS );
        return dataService.daily(tsCode, startDateL.format(SHORT_DATE_FORMATTER), endDateL.format(SHORT_DATE_FORMATTER));
    }







}



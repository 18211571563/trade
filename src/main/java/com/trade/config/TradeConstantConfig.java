package com.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author georgy
 * @Date 2020-02-24 下午 5:12
 * @DESC 交易常量配置
 */
@Component
public class TradeConstantConfig {

    /** 私有选样池 **/
    @Value("${trade.constant.tsCodes}")
    private String[] tsCodes;

    /** 是否使用所有选用池，true的话 tsCodes 失效 **/
    @Value("${trade.constant.usedAll}")
    private Boolean usedAll;

    /** 是否投入资金进行模拟 **/
    @Value("${trade.constant.isUsedCapitail}")
    private Boolean isUsedCapitail;

    /** 今天时间 **/
    @Value("${trade.constant.today}")
    private String today;

    /** 开始时间 **/
    @Value("${trade.constant.startDate}")
    private String startDate;

    /** 结束时间 **/
    @Value("${trade.constant.endDate}")
    private String endDate;

    /** 交易单元 **/
    @Value("${trade.constant.unit}")
    private int unit;

    /** ART平均值 **/
    @Value("${trade.constant.atrPeriod}")
    private int atrPeriod;

    /** 突破开盘价平均值 **/
    @Value("${trade.constant.breakOpenDay}")
    private int breakOpenDay;

    /** 突破止损价平均值 **/
    @Value("${trade.constant.breakCloseDay}")
    private int breakCloseDay;

    /** 大趋势过滤线平均值 **/
    @Value("${trade.constant.filterDay}")
    private int filterDay;

    /** 个人主观市场趋势偏移量 -1 ~ 1 ， -1 看空只做空， 1 看多只做多 **/
    @Value("${trade.constant.marketTrendOffset}")
    private int marketTrendOffset;

    
    public String[] getTsCodes() {
        return tsCodes;
    }

    public void setTsCodes(String[] tsCodes) {
        this.tsCodes = tsCodes;
    }

    public Boolean getUsedAll() {
        return usedAll;
    }

    public void setUsedAll(Boolean usedAll) {
        this.usedAll = usedAll;
    }

    public Boolean getUsedCapitail() {
        return isUsedCapitail;
    }

    public void setUsedCapitail(Boolean usedCapitail) {
        isUsedCapitail = usedCapitail;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getAtrPeriod() {
        return atrPeriod;
    }

    public void setAtrPeriod(int atrPeriod) {
        this.atrPeriod = atrPeriod;
    }

    public int getBreakOpenDay() {
        return breakOpenDay;
    }

    public void setBreakOpenDay(int breakOpenDay) {
        this.breakOpenDay = breakOpenDay;
    }

    public int getBreakCloseDay() {
        return breakCloseDay;
    }

    public void setBreakCloseDay(int breakCloseDay) {
        this.breakCloseDay = breakCloseDay;
    }

    public int getFilterDay() {
        return filterDay;
    }

    public void setFilterDay(int filterDay) {
        this.filterDay = filterDay;
    }

    public int getMarketTrendOffset() {
        return marketTrendOffset;
    }

    public void setMarketTrendOffset(int marketTrendOffset) {
        this.marketTrendOffset = marketTrendOffset;
    }


}

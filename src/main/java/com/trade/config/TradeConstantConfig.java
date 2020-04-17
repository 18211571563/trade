package com.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author georgy
 * @Date 2020-02-24 下午 5:12
 * @DESC 交易常量配置
 */
@Component
public class TradeConstantConfig {

    /** 开仓策略编码 **/
    @Value("${trade.constant.strategy.code.open}")
    private String openStrategyCode;

    /** 止损策略编码 **/
    @Value("${trade.constant.strategy.code.close}")
    private String closeStrategyCode;

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
    private Integer unit;

    /** ART平均值 **/
    @Value("${trade.constant.atrPeriod}")
    private Integer atrPeriod;

    /** 突破开盘价平均值 **/
    @Value("${trade.constant.breakOpenDay}")
    private Integer breakOpenDay;

    /** 突破止损价平均值 **/
    @Value("${trade.constant.breakCloseDay}")
    private Integer breakCloseDay;

    /** 大趋势过滤线平均值 **/
    @Value("${trade.constant.filterDay}")
    private Integer filterDay;

    /** 个人主观市场趋势偏移量 -1 ~ 1 ， -1 看空只做空， 1 看多只做多 **/
    @Value("${trade.constant.marketTrendOffset}")
    private Integer marketTrendOffset;

    /** 总资金 **/
    @Value("${trade.constant.totalCapital}")
    private Integer totalCapital;

    /** 风险系数 **/
    @Value("${trade.constant.riskParameter}")
    private Integer riskParameter;

    public String getOpenStrategyCode() {
        return openStrategyCode;
    }

    public void setOpenStrategyCode(String openStrategyCode) {
        this.openStrategyCode = openStrategyCode;
    }

    public String getCloseStrategyCode() {
        return closeStrategyCode;
    }

    public void setCloseStrategyCode(String closeStrategyCode) {
        this.closeStrategyCode = closeStrategyCode;
    }

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

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getAtrPeriod() {
        return atrPeriod;
    }

    public void setAtrPeriod(Integer atrPeriod) {
        this.atrPeriod = atrPeriod;
    }

    public Integer getBreakOpenDay() {
        return breakOpenDay;
    }

    public void setBreakOpenDay(Integer breakOpenDay) {
        this.breakOpenDay = breakOpenDay;
    }

    public Integer getBreakCloseDay() {
        return breakCloseDay;
    }

    public void setBreakCloseDay(Integer breakCloseDay) {
        this.breakCloseDay = breakCloseDay;
    }

    public Integer getFilterDay() {
        return filterDay;
    }

    public void setFilterDay(Integer filterDay) {
        this.filterDay = filterDay;
    }

    public Integer getMarketTrendOffset() {
        return marketTrendOffset;
    }

    public void setMarketTrendOffset(Integer marketTrendOffset) {
        this.marketTrendOffset = marketTrendOffset;
    }

    public Integer getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(Integer totalCapital) {
        this.totalCapital = totalCapital;
    }

    public Integer getRiskParameter() {
        return riskParameter;
    }

    public void setRiskParameter(Integer riskParameter) {
        this.riskParameter = riskParameter;
    }
}

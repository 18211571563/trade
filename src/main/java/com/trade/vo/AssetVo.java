package com.trade.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @Author georgy
 * @Date 2020-01-14 下午 3:13
 * @DESC TODO
 */
public class AssetVo {

    private BigDecimal totalCapital = BigDecimal.ZERO; // 总资金
    private BigDecimal frozenCapital = BigDecimal.ZERO; // 冻结资金
    private BigDecimal riskParameter = BigDecimal.ZERO; // 风险系数 0.50%


    public synchronized static AssetVo create(BigDecimal totalCapital, BigDecimal riskParameter){
        return new AssetVo(totalCapital, riskParameter);
    }

    public AssetVo(){super();}

    public AssetVo(BigDecimal totalCapital, BigDecimal riskParameter){
        super();
        this.totalCapital = totalCapital;
        this.riskParameter = riskParameter;
    }


    public BigDecimal getTotalCapital() {
        return totalCapital;
    }

    public synchronized void setTotalCapital(BigDecimal totalCapital) {
        this.totalCapital = totalCapital;
    }

    public BigDecimal getUsableCapital() {
        return this.totalCapital.subtract(this.frozenCapital);
    }


    public synchronized BigDecimal getFrozenCapital() {
        return frozenCapital;
    }

    public synchronized void setFrozenCapital(BigDecimal frozenCapital) {
        this.frozenCapital = frozenCapital;
    }

    public BigDecimal getRiskParameter() {
        return riskParameter;
    }

    public synchronized void setRiskParameter(BigDecimal riskParameter) {
        this.riskParameter = riskParameter;
    }
}

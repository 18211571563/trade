package com.trade.vo;

import java.math.BigDecimal;

/**
 * @Author georgy
 * @Date 2020-01-14 下午 3:13
 * @DESC TODO
 */
public class AssetVo {

    public AssetVo(){super();}
    public AssetVo(BigDecimal totalCapital, BigDecimal riskParameter){
        super();
        this.totalCapital = totalCapital;
        this.riskParameter = riskParameter;
    }

    private BigDecimal totalCapital; // 总资金
    private BigDecimal riskParameter; // 风险系数 0.50%

    public BigDecimal getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(BigDecimal totalCapital) {
        this.totalCapital = totalCapital;
    }

    public BigDecimal getRiskParameter() {
        return riskParameter;
    }

    public void setRiskParameter(BigDecimal riskParameter) {
        this.riskParameter = riskParameter;
    }
}

package com.trade.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-10 下午 3:26
 * @DESC TODO
 */
public class CapitalUtil {

    /**
     * 获取容许交易量（手）
     * @param totalCapital 总资金
     * @param riskParameter 风险系数
     * @param atr   真实波动幅度
     * @param unit  交易单元，如一手100
     * @return
     */
    public int getTradeVolume(BigDecimal totalCapital, BigDecimal riskParameter, BigDecimal atr, int unit){
        BigDecimal tradeCapital = CapitalUtil.getTradeCapital(totalCapital, riskParameter);
        return tradeCapital.divide(atr.multiply(BigDecimal.valueOf(unit)), 0, BigDecimal.ROUND_DOWN).intValue();
    }

    /**
     * 获取本次容许最大的交易额
     * @param totalCapital     总资金
     * @param riskParameter    风险系数
     * @return
     */
    public static BigDecimal getTradeCapital(BigDecimal totalCapital, BigDecimal riskParameter){
        return totalCapital.multiply(riskParameter).divide(BigDecimal.valueOf(10000), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * ATR
     * @param highs 最高价 n
     * @param lows  最低价 n
     * @param closes 昨收  n+1
     * @param period 周期
     * @return
     */
    public static BigDecimal atr(List<BigDecimal> highs, List<BigDecimal> lows, List<BigDecimal> closes, int period){
        List<BigDecimal> trList = new ArrayList<>();
        for(int i = 0; i < period; i++){
            double hl = Math.abs(highs.get(i).subtract(lows.get(i)).doubleValue());
            double hc = Math.abs(highs.get(i).subtract(closes.get(i)).doubleValue());
            double lc = Math.abs(lows.get(i).subtract(closes.get(i)).doubleValue());

            double tr = Math.max(Math.max(hl, hc), lc); // 获取最大波动
            trList.add(BigDecimal.valueOf(tr));
        }

        double average = trList.stream().mapToDouble(BigDecimal::doubleValue).average().getAsDouble();
        return BigDecimal.valueOf(average);
    }

}

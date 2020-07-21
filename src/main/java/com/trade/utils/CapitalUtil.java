package com.trade.utils;

import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-10 下午 3:26
 * @DESC 计算工具类
 */
public class CapitalUtil {

    /**
     * 获取最大值
     * @param datas
     * @return
     */
    public static DailyVo getMax(List<DailyVo> datas){
        DailyVo data = null;
        for (DailyVo dailyVo : datas) {
            if(data == null ) data = dailyVo;
            if(new BigDecimal(dailyVo.getClose()).compareTo(new BigDecimal(data.getClose())) > 0){
                data = dailyVo;
            }
         }
        return data;
    }

    /**
     * 获取最小值
     * @param datas
     * @return
     */
    public static DailyVo getMin(List<DailyVo> datas){
        DailyVo data = null;
        for (DailyVo dailyVo : datas) {
            if(data == null ) data = dailyVo;
            if(new BigDecimal(dailyVo.getClose()).compareTo(new BigDecimal(data.getClose())) < 0){
                data = dailyVo;
            }
        }
        return data;
    }

    /**
     * 计算BP
     * @param daily
     * @param orderVo
     * @return
     */
    public static BigDecimal calcBp(DailyVo daily, OrderVo orderVo) {
        return CapitalUtil.calcBp(orderVo.getDirection(), orderVo.getPrice(), orderVo.getVolume(), daily.getClose());
    }

    /**
     * 计算BP
     * @param direction
     * @param price
     * @param volume
     * @param close
     * @return
     */
    public static BigDecimal calcBp(int direction,BigDecimal price, BigDecimal volume, String close) {
        BigDecimal bp = BigDecimal.ZERO;
        // 计算交易损益(BP)
        if(direction == 1){ // 多头头寸平仓计算 BP
            bp = new BigDecimal(close).subtract(price).multiply(volume);

        }else if(direction == 0){ // 空头头寸平仓计算 BP
            bp = price.subtract(new BigDecimal(close)).multiply(volume);

        }else{
            throw new RuntimeException("数据错误: 交易订单没有方向");
        }
        return bp;
    }

    /**
     * 计算BP_Rate
     * @param daily
     * @param orderVo
     * @return
     */
    public static BigDecimal calcBpRate(DailyVo daily, OrderVo orderVo) {
        return CapitalUtil.calcBpRate(orderVo.getDirection(), orderVo.getPrice(), daily.getClose());
    }

    /**
     * 计算BP_Rate
     * @param direction
     * @param price
     * @param close
     * @return
     */
    public static BigDecimal calcBpRate(int direction, BigDecimal price, String close) {
        BigDecimal bp_rate = BigDecimal.ZERO;
        // 计算交易损益(BP)
        if(direction == 1){ // 多头头寸平仓计算 BP
            bp_rate = new BigDecimal(close).subtract(price).divide(price, 2, BigDecimal.ROUND_HALF_UP);

        }else if(direction == 0){ // 空头头寸平仓计算 BP
            bp_rate = price.subtract(new BigDecimal(close)).divide(price, 2, BigDecimal.ROUND_HALF_UP);

        }else{
            throw new RuntimeException("数据错误: 交易订单没有方向");
        }
        return bp_rate;
    }



    /**
     * 计算每日 atr
     * @param datas 数据集合
     * @return
     */
    public static BigDecimal getDailyAtr(List<DailyVo> datas) {

        List<BigDecimal> highs = new ArrayList<>();
        List<BigDecimal> lows = new ArrayList<>();
        List<BigDecimal> closes = new ArrayList<>();

        for (int i = datas.size() - 1; i >= 0; i--) {
            if(i == (datas.size() - 1) ) continue;
            DailyVo yesterdayDailyVo = datas.get(i + 1); // 昨天
            DailyVo todayDailyVo = datas.get(i);         // 今天

            highs.add(new BigDecimal(todayDailyVo.getHigh()));
            lows.add(new BigDecimal(todayDailyVo.getLow()));
            closes.add(new BigDecimal(yesterdayDailyVo.getClose()));
        }

        BigDecimal atr = CapitalUtil.atr(highs, lows, closes);
        return atr;
    }

    /**
     * 获取容许交易量（手）
     * @param totalCapital 总资金
     * @param riskParameter 风险系数
     * @param closeDeep 止损深度
     * @param atr   真实波动幅度
     * @param unit  交易单元，如一手100
     * @return
     */
    public static int getTradeVolume(BigDecimal totalCapital, BigDecimal riskParameter, BigDecimal closeDeep, BigDecimal atr, int unit){
        BigDecimal tradeCapital = CapitalUtil.getTradeCapital(totalCapital, riskParameter);
        return tradeCapital.divide(atr.multiply(closeDeep).multiply(BigDecimal.valueOf(unit)), 0, BigDecimal.ROUND_DOWN).intValue();
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
     * @return
     */
    public static BigDecimal atr(List<BigDecimal> highs, List<BigDecimal> lows, List<BigDecimal> closes){
        if(lows.size() != highs.size() || lows.size() != closes.size()){throw new RuntimeException("ATR数据错误");}
        int period = lows.size();

        List<BigDecimal> trList = new ArrayList<>();
        for(int i = 0; i < period; i++){
            double hl = Math.abs(highs.get(i).subtract(lows.get(i)).doubleValue());
            double hc = Math.abs(highs.get(i).subtract(closes.get(i)).doubleValue());
            double lc = Math.abs(lows.get(i).subtract(closes.get(i)).doubleValue());

            double tr = Math.max(Math.max(hl, hc), lc); // 获取最大波动
            trList.add(BigDecimal.valueOf(tr));
        }

        double average = CapitalUtil.average(trList);
        return BigDecimal.valueOf(average);
    }

    /**
     * 计算均值
     * @param trList
     * @return
     */
    public static double average(List<BigDecimal> trList) {
        return trList.stream().mapToDouble(BigDecimal::doubleValue).average().getAsDouble();
    }

}

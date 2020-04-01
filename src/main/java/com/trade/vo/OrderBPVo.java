package com.trade.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author georgy
 * @Date 2020-03-31 下午 5:43
 * @DESC 订单BP 对象
 */
public class OrderBPVo {

    private String tsCode;
    private int direction; // 0: 空， 1: 多
    private BigDecimal bp;
    private BigDecimal bpRate;
    private String tradeDate;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal volume;

    public OrderBPVo(){super();}
    public OrderBPVo(String tsCode,
                     int direction,
                     BigDecimal bp,
                     BigDecimal bpRate,
                     String tradeDate,
                     BigDecimal open,
                     BigDecimal close,
                     BigDecimal volume){
        super();
        this.tsCode = tsCode;
        this.direction = direction;
        this.bp = bp;
        this.bpRate = bpRate;
        this.tradeDate = tradeDate;
        this.open = open;
        this.close = close;
        this.volume = volume;


    }

    public String getTsCode() {
        return tsCode;
    }

    public void setTsCode(String tsCode) {
        this.tsCode = tsCode;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public BigDecimal getBp() {
        return bp;
    }

    public void setBp(BigDecimal bp) {
        this.bp = bp;
    }

    public BigDecimal getBpRate() {
        return bpRate;
    }

    public void setBpRate(BigDecimal bpRate) {
        this.bpRate = bpRate;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
}

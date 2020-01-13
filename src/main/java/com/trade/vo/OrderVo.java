package com.trade.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author georgy
 * @Date 2020-01-13 下午 5:15
 * @DESC TODO
 */
public class OrderVo {

    public OrderVo(){
        super();
    }

    public OrderVo(String tsCode, int direction, BigDecimal price, BigDecimal volume, LocalDateTime time ){
        super();
        this.tsCode = tsCode;
        this.direction = direction;
        this.price = price;
        this.volume = volume;
        this.time = time;
    }

    private String tsCode;
    private int direction; // 0: 空， 1: 多
    private BigDecimal price;
    private BigDecimal volume;
    private LocalDateTime time;

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getTsCode() {
        return tsCode;
    }

    public void setTsCode(String tsCode) {
        this.tsCode = tsCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}

package com.trade.vo;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author georgy
 * @Date 2020-01-13 下午 2:23
 * @DESC TODO
 */
public class TradeDateVo implements Serializable {

    public TradeDateVo(){
        super();
    }

    public TradeDateVo(String exchange, String calDate){
        super();
        this.exchange = exchange;
        this.calDate = calDate;
    }

    private static final long serialVersionUID = 24375451280286L;

    private String exchange;
    private String calDate;
    private String isOpen;
    private String pretradeDate;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCalDate() {
        return calDate;
    }

    public void setCalDate(String calDate) {
        this.calDate = calDate;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getPretradeDate() {
        return pretradeDate;
    }

    public void setPretradeDate(String pretradeDate) {
        this.pretradeDate = pretradeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeDateVo that = (TradeDateVo) o;
        return exchange.equals(that.exchange) &&
                calDate.equals(that.calDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchange, calDate);
    }
}

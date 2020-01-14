package com.trade.vo;

import java.io.Serializable;

/**
 * @Author georgy
 * @Date 2020-01-10 下午 3:12
 * @DESC TODO
 */
public class DailyVo implements Serializable {

    private static final long serialVersionUID = 76575456131234L;

    private String ts_code	    ;// 股票代码
    private String trade_date   ;// 交易日期
    private String open	        ;// 开盘价
    private String high	        ;// 最高价
    private String low	        ;// 最低价
    private String close	    ;// 收盘价
    private String pre_close    ;// 昨收价
    private String change	    ;// 涨跌额
    private String pct_chg	    ;// 涨跌幅 （未复权，如果是复权请用 通用行情接口 ）
    private String vol	        ;// 成交量 （手）
    private String amount	    ;// 成交额 （千元）

    public String getTs_code() {
        return ts_code;
    }

    public void setTs_code(String ts_code) {
        this.ts_code = ts_code;
    }

    public String getTrade_date() {
        return trade_date;
    }

    public void setTrade_date(String trade_date) {
        this.trade_date = trade_date;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getPre_close() {
        return pre_close;
    }

    public void setPre_close(String pre_close) {
        this.pre_close = pre_close;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getPct_chg() {
        return pct_chg;
    }

    public void setPct_chg(String pct_chg) {
        this.pct_chg = pct_chg;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}

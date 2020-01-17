package com.trade.vo;

import java.io.Serializable;

/**
 * @Author georgy
 * @Date 2020-01-17 下午 12:39
 * @DESC TODO
 */
public class StockBasicVo implements Serializable {

    private static final long serialVersionUID = 77375441298261L;

    private String ts_code      ; // TS代码
    private String symbol       ; // 股票代码
    private String name         ; // 股票名称
    private String area         ; // 所在地域
    private String industry     ; // 所属行业
    private String fullname     ; // 股票全称
    private String enname       ; // 英文全称
    private String market       ; // 市场类型 （主板/中小板/创业板/科创板）
    private String exchange     ; // 交易所代码
    private String curr_type    ; // 交易货币
    private String list_status  ; // 上市状态： L上市 D退市 P暂停上市
    private String list_date    ; // 上市日期
    private String delist_date  ; // 退市日期
    private String is_hs        ; // 是否沪深港通标的，N否 H沪股通 S深股通

    public String getTs_code() {
        return ts_code;
    }

    public void setTs_code(String ts_code) {
        this.ts_code = ts_code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCurr_type() {
        return curr_type;
    }

    public void setCurr_type(String curr_type) {
        this.curr_type = curr_type;
    }

    public String getList_status() {
        return list_status;
    }

    public void setList_status(String list_status) {
        this.list_status = list_status;
    }

    public String getList_date() {
        return list_date;
    }

    public void setList_date(String list_date) {
        this.list_date = list_date;
    }

    public String getDelist_date() {
        return delist_date;
    }

    public void setDelist_date(String delist_date) {
        this.delist_date = delist_date;
    }

    public String getIs_hs() {
        return is_hs;
    }

    public void setIs_hs(String is_hs) {
        this.is_hs = is_hs;
    }
}

package com.trade.vo;

import java.io.Serializable;

/**
 * @Author georgy
 * @Date 2020-01-17 下午 12:39
 * @DESC TODO
 */
public class IndexBasicVo implements Serializable {

    private static final long serialVersionUID = 77375464129111L;

    private String ts_code		;   // TS代码
    private String name		    ;   // 简称
    private String fullname	    ;   // 指数全称
    private String market		;   // 市场
    private String publisher	;   // 发布方
    private String index_type	;   // 指数风格
    private String category	    ;   // 指数类别
    private String base_date	;   // 基期
    private String base_point	;   // 基点
    private String list_date	;   // 发布日期
    private String weight_rule	;   // 加权方式
    private String desc		    ;   // 描述
    private String exp_date	    ;   // 终止日期

    public String getTs_code() {
        return ts_code;
    }

    public void setTs_code(String ts_code) {
        this.ts_code = ts_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIndex_type() {
        return index_type;
    }

    public void setIndex_type(String index_type) {
        this.index_type = index_type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBase_date() {
        return base_date;
    }

    public void setBase_date(String base_date) {
        this.base_date = base_date;
    }

    public String getBase_point() {
        return base_point;
    }

    public void setBase_point(String base_point) {
        this.base_point = base_point;
    }

    public String getList_date() {
        return list_date;
    }

    public void setList_date(String list_date) {
        this.list_date = list_date;
    }

    public String getWeight_rule() {
        return weight_rule;
    }

    public void setWeight_rule(String weight_rule) {
        this.weight_rule = weight_rule;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExp_date() {
        return exp_date;
    }

    public void setExp_date(String exp_date) {
        this.exp_date = exp_date;
    }
}

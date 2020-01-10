package com.trade.service;

import com.trade.vo.DailyVo;

import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-10 下午 2:59
 * @DESC TODO
 */
public interface DataService {
    List<DailyVo> daily(String ts_code, String start_date, String end_date);
}

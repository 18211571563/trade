package com.trade.service;

import com.trade.vo.DailyVo;
import com.trade.vo.TradeDateVo;

import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-10 下午 2:59
 * @DESC TODO
 */
public interface DataService {
    List<TradeDateVo> tradeCal(String exchange, String start_date, String end_date);

    boolean tradeCal(String start_date);

    List<DailyVo> daily(String ts_code, String start_date, String end_date);

    List<DailyVo> daily(String ts_code, String start_date, int back_day);
}

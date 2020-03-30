package com.trade.service.common;

import com.trade.vo.DailyVo;
import com.trade.vo.StockBasicVo;
import com.trade.vo.TradeDateVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-10 下午 2:59
 * @DESC TODO
 */
public interface DataService {

    List<StockBasicVo> stock_basic();

    List<TradeDateVo> tradeCal(String exchange, String start_date, String end_date);

    boolean tradeCal(String start_date);

    List<DailyVo> daily(String ts_code, String start_date, String end_date);

    List<DailyVo> daily(String ts_code, String start_date, int back_day);


}

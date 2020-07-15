package com.trade.memory_storage;

import com.trade.vo.DailyVo;
import com.trade.vo.TradeDateVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author georgy
 * @Date 2020-07-15 上午 9:57
 * @DESC 内存仓库
 */
public class MemoryStorage {

    public Map<String, List<DailyVo>> dailyVoMaps = new HashMap<>();
    public List<String> dailyTradeDateList = new ArrayList<>();

    public List<TradeDateVo> tradeDateVoList = new ArrayList<>();

    public static ThreadLocal<MemoryStorage> memoryStorageThreadLocal = new ThreadLocal<>();


    public Map<String, List<DailyVo>> getDailyVoMaps() {
        return dailyVoMaps;
    }

    public void setDailyVoMaps(Map<String, List<DailyVo>> dailyVoMaps) {
        this.dailyVoMaps = dailyVoMaps;
    }

    public List<String> getDailyTradeDateList() {
        return dailyTradeDateList;
    }

    public void setDailyTradeDateList(List<String> dailyTradeDateList) {
        this.dailyTradeDateList = dailyTradeDateList;
    }

    public List<TradeDateVo> getTradeDateVoList() {
        return tradeDateVoList;
    }

    public void setTradeDateVoList(List<TradeDateVo> tradeDateVoList) {
        this.tradeDateVoList = tradeDateVoList;
    }
}

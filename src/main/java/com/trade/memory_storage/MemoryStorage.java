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

    public static Map<String, Map<String, DailyVo>> dailyVosMap = new HashMap<>();
    public static List<TradeDateVo> tradeDateVoList = new ArrayList<>();

}

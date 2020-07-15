package com.trade.service.common.impl;

import com.trade.memory_storage.MemoryStorage;
import com.trade.service.common.DataService;
import com.trade.utils.CommonUtil;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.IndexBasicVo;
import com.trade.vo.StockBasicVo;
import com.trade.vo.TradeDateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-07-15 上午 10:04
 * @DESC TODO
 */
@Service
@Primary
public class MemoryDataServiceImpl implements DataService {

    @Autowired
    @Qualifier("mongoDataServiceImpl")
    private DataService dataService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<StockBasicVo> stock_basic() {
        return dataService.stock_basic();
    }

    @Override
    public List<IndexBasicVo> index_basic() {
        return dataService.index_basic();
    }

    @Override
    public List<TradeDateVo> tradeCal(String exchange, String start_date, String end_date) {
        MemoryStorage memoryStorage = MemoryStorage.memoryStorageThreadLocal.get();
        List<TradeDateVo> tradeDateVoList = memoryStorage.getTradeDateVoList();
        int start_date_i = tradeDateVoList.indexOf(new TradeDateVo(exchange, start_date));
        int end_date_i = tradeDateVoList.indexOf(new TradeDateVo(exchange, end_date));
        if(start_date_i == -1 ) return null;
        return tradeDateVoList.subList(start_date_i, end_date_i + 1 );
    }

    @Override
    public boolean tradeCal(String start_date) {
        List<TradeDateVo> tradeDateVos = this.tradeCal("SSE", start_date, start_date);
        if(!CollectionUtils.isEmpty(tradeDateVos)){
            TradeDateVo tradeDateVo = tradeDateVos.get(0);
            return tradeDateVo != null && tradeDateVo.getIsOpen().equals("1");
        }
        return false;
    }

    @Override
    public List<DailyVo> daily(String ts_code, String start_date, String end_date) {
        MemoryStorage memoryStorage = MemoryStorage.memoryStorageThreadLocal.get();

        List<DailyVo> dailyVos = memoryStorage.dailyVoMaps.get(ts_code);

        int start_date_i = CommonUtil.getValidIndexToList(memoryStorage.dailyTradeDateList,
                LocalDate.parse(start_date, TimeUtil.SHORT_DATE_FORMATTER), 100, 1);
        int end_date_i = CommonUtil.getValidIndexToList(memoryStorage.dailyTradeDateList,
                LocalDate.parse(end_date, TimeUtil.SHORT_DATE_FORMATTER), 100, -1);

        if(start_date_i != -1 && end_date_i != -1 && !CollectionUtils.isEmpty(dailyVos)){
            List<DailyVo> datas = dailyVos.subList(end_date_i, start_date_i + 1);
            return datas;
        }

        return null;
    }

    @Override
    public List<DailyVo> daily(String ts_code, String start_date, int back_day) {
        MemoryStorage memoryStorage = MemoryStorage.memoryStorageThreadLocal.get();
        List<DailyVo> dailyVos = memoryStorage.dailyVoMaps.get(ts_code);
        int i = memoryStorage.dailyTradeDateList.indexOf(start_date);

        if(i != -1 && !CollectionUtils.isEmpty(dailyVos)){
            List<DailyVo> datas = dailyVos.subList(i + 1 , i + 1 + back_day);
            return datas;
        }
        return null;
    }
}

package com.trade.service.common.impl;

import com.trade.ResourceManager.ThreadPoolManager;
import com.trade.aspect.CommonAspect;
import com.trade.config.TradeConstantConfig;
import com.trade.memory_storage.MemoryStorage;
import com.trade.service.common.DataService;
import com.trade.service.common.MemoryService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author georgy
 * @Date 2020-07-15 上午 10:04
 * @DESC TODO
 */
@Service
@Primary
public class DailyMemoryDataServiceImpl implements DataService,MemoryService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("mongoDataServiceImpl")
    private DataService dataService;

    @Autowired
    @Qualifier("dailyMemoryDataServiceImpl")
    private MemoryService memoryService;

    @Autowired
    private ThreadPoolManager threadPoolManager;    // 线程池
    @Autowired
    private TradeConstantConfig tradeConstantConfig;    // 交易常量


    /**
     * 加载数据
     */
    @Override
    public void load(){
        /** 标的池 **/
        MemoryStorage.stockBasicVoList = dataService.stock_basic();

        /** 交易时间 **/
        String startDate = LocalDate.parse(tradeConstantConfig.getStartDate(), TimeUtil.SHORT_DATE_FORMATTER).minus(tradeConstantConfig.getOffset() + 30, ChronoUnit.DAYS).format(TimeUtil.SHORT_DATE_FORMATTER);
        String endDate = tradeConstantConfig.getEndDate();
        List<TradeDateVo> tradeDateVos = dataService.tradeCal("SSE", startDate, endDate);
        MemoryStorage.tradeDateVoList = tradeDateVos;

        /** 按照 daily 储存数据 **/
        MemoryStorage.tradeDateVoList.forEach(tradeDateVo -> {
            if(tradeDateVo != null && tradeDateVo.getIsOpen().equals("1")){
                Date date = new Date();
                String calDate = tradeDateVo.getCalDate();
                List<DailyVo> dailys = dataService.daily("ALL", calDate, calDate);
                Map<String, DailyVo> tscodeDailyMap = new HashMap<>();
                for (DailyVo daily : dailys) {
                    tscodeDailyMap.put(daily.getTs_code(), daily);
                }
                MemoryStorage.dailyVosMap.put(calDate, tscodeDailyMap);
            }
        });
        logger.info("内存加载完成!");
    }

    @Override
    public void asyncLoad() {
        if(CommonAspect.load) throw new RuntimeException("程序运行中，请莫重复运行！");
        threadPoolManager.getProcessSingleExecutorService().execute(() -> {
            try {
                memoryService.load();
            } catch (Exception e) {
                logger.error("内存数据加载失败:{}", e);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void clear() {
        MemoryStorage.dailyVosMap.clear();
        MemoryStorage.tradeDateVoList.clear();
        logger.info("内存清理完成!");
    }

    @Override
    public List<StockBasicVo> stock_basic() {
        return MemoryStorage.stockBasicVoList;
    }

    @Override
    public List<IndexBasicVo> index_basic() {
        return dataService.index_basic();
    }

    @Override
    public List<TradeDateVo> tradeCal(String exchange, String start_date, String end_date) {
        List<TradeDateVo> tradeDateVoList = MemoryStorage.tradeDateVoList;
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

        List<String> dailyTradeDateList = MemoryStorage.tradeDateVoList.stream().map(a -> a.getCalDate()).collect(Collectors.toList());
        int start_date_i = CommonUtil.getValidIndexToList(dailyTradeDateList,
                LocalDate.parse(start_date, TimeUtil.SHORT_DATE_FORMATTER), 100, -1);
        int end_date_i = CommonUtil.getValidIndexToList(dailyTradeDateList,
                LocalDate.parse(end_date, TimeUtil.SHORT_DATE_FORMATTER), 100, 1);
        int start_date_i_int = Integer.parseInt(dailyTradeDateList.get(start_date_i));
        int end_date_i_int = Integer.parseInt(dailyTradeDateList.get(end_date_i));

        List<DailyVo> datas = new ArrayList<>();
        for (int i = start_date_i_int; i <= end_date_i_int; i++){
            Map<String, DailyVo> stringDailyVoMap = MemoryStorage.dailyVosMap.get(String.valueOf(i));
            if(stringDailyVoMap != null){
                DailyVo dailyVo = stringDailyVoMap.get(ts_code);
                if(dailyVo != null) datas.add(dailyVo);
            }
        }
        Collections.reverse(datas);
        return datas;
    }

    @Override
    public List<DailyVo> daily(String ts_code, String start_date, int back_day) {
        List<DailyVo> datas = new ArrayList<>();
        // 根据当前时间计算出延后的时间限制
        List<String> dailyTradeDateList = MemoryStorage.tradeDateVoList.stream().map(a -> a.getCalDate()).collect(Collectors.toList());
        int i = dailyTradeDateList.indexOf(start_date);
        if(i != -1){
            int bindex = (i - back_day * 2) < 0? 0 : i - (back_day * 2);
            List<String> subDailyTradeDateList = dailyTradeDateList.subList(bindex, i); // 包含非交易日
            for (String tradeDateVo : subDailyTradeDateList) {
                Map<String, DailyVo> stringDailyVoMap = MemoryStorage.dailyVosMap.get(tradeDateVo);
                if(stringDailyVoMap != null){
                    DailyVo dailyVo = stringDailyVoMap.get(ts_code);
                    if(dailyVo != null) datas.add(dailyVo);
                }
            }
            if(datas.size() >= back_day) {
                datas = datas.subList(datas.size() - back_day, datas.size());
                Collections.reverse(datas);
                return datas;
            }
        }

        return null;
    }
}

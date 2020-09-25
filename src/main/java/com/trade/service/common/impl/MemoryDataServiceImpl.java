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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author georgy
 * @Date 2020-07-15 上午 10:04
 * @DESC TODO
 */
@Service
@Primary
public class MemoryDataServiceImpl implements DataService, MemoryService {


    @Autowired
    private MemoryService memoryService;
    @Autowired
    @Qualifier("mongoDataServiceImpl")
    private DataService dataService;
    @Autowired
    private ThreadPoolManager threadPoolManager;

    @Autowired
    private TradeConstantConfig tradeConstantConfig;

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 加载数据
     */
    @Override
    public void load(){
        // 日间数据
        List<StockBasicVo> stockBasicVos = dataService.stock_basic();
        stockBasicVos.forEach(stockBasicVo -> {
            String tsCode = stockBasicVo.getTs_code();
            this.initMemoryStockBasicVo(tsCode);
        });

        // 交易时间
        List<TradeDateVo> tradeDateVos = dataService.tradeCal("SSE", tradeConstantConfig.getStartDate(), tradeConstantConfig.getEndDate());
        MemoryStorage.tradeDateVoList = tradeDateVos;
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

    /**
     * 初始化 StockBasicVo 数据 到 本地内存
     * @param tsCode
     * @return
     */
    private void initMemoryStockBasicVo(String tsCode) {

        // 初始化 日间数据
        String startDate = LocalDate.parse(tradeConstantConfig.getStartDate(), TimeUtil.SHORT_DATE_FORMATTER).minus(tradeConstantConfig.getOffset() + 30, ChronoUnit.DAYS).format(TimeUtil.SHORT_DATE_FORMATTER);
        String endDate = tradeConstantConfig.getEndDate();
        List<DailyVo> dailys = dataService.daily(tsCode, startDate, endDate);
        MemoryStorage.dailyVosMap.put(tsCode, dailys);
    }

    @Override
    public List<StockBasicVo> stock_basic() {
        return dataService.stock_basic();
    }

    @Override
    public List<IndexBasicVo> index_basic() {
        return dataService.index_basic();
    }

    /**
     * 交易日历
     * @param exchange
     * @param start_date
     * @param end_date
     * @return
     */
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

        List<DailyVo> dailyVos = MemoryStorage.dailyVosMap.get(ts_code);

        List<String> dailyTradeDateList = dailyVos.stream().map(a -> a.getTrade_date()).collect(Collectors.toList());

        int start_date_i = CommonUtil.getValidIndexToList(dailyTradeDateList,
                LocalDate.parse(start_date, TimeUtil.SHORT_DATE_FORMATTER), 100, -1);
        int end_date_i = CommonUtil.getValidIndexToList(dailyTradeDateList,
                LocalDate.parse(end_date, TimeUtil.SHORT_DATE_FORMATTER), 100, 1);

        if(start_date_i != -1 && end_date_i != -1 && !CollectionUtils.isEmpty(dailyVos)){
            List<DailyVo> datas = dailyVos.subList(end_date_i, start_date_i + 1);
            return datas;
        }

        return null;
    }

    @Override
    public List<DailyVo> daily(String ts_code, String start_date, int back_day) {
        List<DailyVo> dailyVos = MemoryStorage.dailyVosMap.get(ts_code);

        List<String> dailyTradeDateList = dailyVos.stream().map(a -> a.getTrade_date()).collect(Collectors.toList());
        int i = dailyTradeDateList.indexOf(start_date);

        if(dailyVos.size() >= (i + 1 + back_day) ){
            if(i != -1 && !CollectionUtils.isEmpty(dailyVos)){
                List<DailyVo> datas = dailyVos.subList(i + 1 , i + 1 + back_day);
                return datas;
            }
        }

        return null;
    }
}

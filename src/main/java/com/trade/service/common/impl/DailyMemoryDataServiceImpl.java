package com.trade.service.common.impl;

import com.trade.ResourceManager.ThreadPoolManager;
import com.trade.aspect.CommonAspect;
import com.trade.config.TradeConstantConfig;
import com.trade.memory_storage.MemoryStorage;
import com.trade.service.common.DataService;
import com.trade.service.common.MemoryService;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.TradeDateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author georgy
 * @Date 2020-07-15 上午 10:04
 * @DESC TODO
 */
@Service
public class DailyMemoryDataServiceImpl implements MemoryService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("mongoDataServiceImpl")
    private DataService dataService;

    @Autowired
    private ThreadPoolManager threadPoolManager;    // 线程池
    @Autowired
    private TradeConstantConfig tradeConstantConfig;    // 交易常量


    /**
     * 加载数据
     */
    @Override
    public void load(){
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
                MemoryStorage.dailyVosMap.put(calDate, dailys);
                logger.info(String.valueOf(new Date().getTime() - date.getTime()));
            }
        });
        logger.info("内存加载完成!");
    }

    @Override
    public void asyncLoad() {
        if(CommonAspect.load) throw new RuntimeException("程序运行中，请莫重复运行！");
        threadPoolManager.getProcessSingleExecutorService().execute(() -> {
            try {
                this.load();
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


//    @Override
//    public List<DailyVo> daily(String start_date, int back_day) {
//        List<DailyVo> dailyVos = MemoryStorage.dailyVosMap.get(ts_code);
//
//        List<String> dailyTradeDateList = dailyVos.stream().map(a -> a.getTrade_date()).collect(Collectors.toList());
//        int i = dailyTradeDateList.indexOf(start_date);
//
//        if(dailyVos.size() >= (i + 1 + back_day) ){
//            if(i != -1 && !CollectionUtils.isEmpty(dailyVos)){
//                List<DailyVo> datas = dailyVos.subList(i + 1 , i + 1 + back_day);
//                return datas;
//            }
//        }
//
//        return null;
//    }
}

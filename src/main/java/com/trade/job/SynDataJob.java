package com.trade.job;

import com.trade.service.DataService;
import com.trade.vo.DailyVo;
import com.trade.vo.StockBasicVo;
import com.trade.vo.TradeDateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @Author georgy
 * @Date 2020-01-17 上午 10:54
 * @DESC TODO
 */
@Component
public class SynDataJob {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("dataServiceImpl")
    private DataService dataService;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 同步交易日历
     * @param start_date
     * @param end_date
     */
    public void tradeCalSym(String start_date, String end_date){
        List<TradeDateVo> sse = dataService.tradeCal("SSE", start_date, end_date);
        mongoTemplate.dropCollection("trade_cal");
        mongoTemplate.insert(sse, "trade_cal");
    }

    public void dailyOnlySym(String start_date, String end_date){
        List<StockBasicVo> stockBasicVos = dataService.stock_basic();
        int index = 0;
        stockBasicVos.forEach(stockBasicVo -> {
            List<DailyVo> dailys = dataService.daily(stockBasicVo.getTs_code(), start_date, end_date);
            mongoTemplate.insert(dailys, "daily");
            logger.info("第{}条, 编码:{}", index, stockBasicVo.getTs_code());
        });
    }

    public void stockBasicSym(){
        List<StockBasicVo> stockBasicVos = dataService.stock_basic();
        mongoTemplate.insert(stockBasicVos, "stock_basic");
    }


}

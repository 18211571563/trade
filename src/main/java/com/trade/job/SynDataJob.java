package com.trade.job;

import com.trade.config.IndexMarketConstantConfig;
import com.trade.service.common.DataService;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.StockBasicVo;
import com.trade.vo.TradeDateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    @Autowired
    private IndexMarketConstantConfig indexMarketConstantConfig;

    /**
     * 同步交易日历
     */
    public void tradeCalSym(){
        List<TradeDateVo> sse = dataService.tradeCal("SSE", "20100101", LocalDate.now().format(TimeUtil.SHORT_DATE_FORMATTER));
        mongoTemplate.dropCollection("trade_cal");
        mongoTemplate.insert(sse, "trade_cal");
    }

    /**
     * 同步标的列表
     */
    public void stockBasicSym(){
        List<StockBasicVo> stockBasicVos = dataService.stock_basic();
        mongoTemplate.dropCollection("stock_basic");
        mongoTemplate.insert(stockBasicVos, "stock_basic");
    }

    /**
     * 同步日间数据
     * @param start_date
     * @param end_date
     */
    public void dailyOnlySym(String start_date, String end_date){

        if(!start_date.equals(end_date)){
            mongoTemplate.dropCollection("daily");
        }

        /** 获取标的信息 begin **/
        List<String> tsCodes = new ArrayList<>();
        {
            // 获取股票标的
            List<StockBasicVo> stockBasicVos = dataService.stock_basic();
            tsCodes.addAll(stockBasicVos.stream().map(StockBasicVo::getTs_code).collect(Collectors.toList()));

            // 获取指数标的
            tsCodes.addAll(indexMarketConstantConfig.getUsed_index_basic_tsCodes().keySet());
        }
        /** end **/

        // 同步每个标的
        int index = 0;
        for (String  ts_code : tsCodes) {
            try {
                List<DailyVo> dailys = dataService.daily(ts_code, start_date, end_date);
                mongoTemplate.insert(dailys, "daily");
                logger.info("第{}条, 编码:{}", String.valueOf(++index), ts_code);
            }catch (Exception e){
                logger.info("第{}条, 编码:{},发生异常:", String.valueOf(++index), ts_code, e);
            }
        }


    }



}

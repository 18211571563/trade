package com.trade.service.common.impl;

import com.trade.service.common.DataService;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.StockBasicVo;
import com.trade.vo.TradeDateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-17 上午 10:39
 * @DESC TODO
 */
@Service
@Primary
public class MongoDataServiceImpl implements DataService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<StockBasicVo> stock_basic() {
        return mongoTemplate.findAll(StockBasicVo.class, "stock_basic");
    }

    /**
     * 获取 start_date 到 end_date 的 交易日 数据
     * @param exchange
     * @param start_date
     * @param end_date
     * @return
     */
    @Override
    public List<TradeDateVo> tradeCal(String exchange, String start_date, String end_date) {
        Query query = new Query(
                Criteria.where("exchange").is(exchange)
                        .andOperator(
                                Criteria.where("calDate").gte(start_date),
                                Criteria.where("calDate").lte(end_date) ));
        List<TradeDateVo> trade_cals = mongoTemplate.find(query, TradeDateVo.class, "trade_cal");
        return trade_cals;
    }

    /**
     * 判断 start_date 时候交易日
     * @param start_date
     * @return
     */
    @Override
    public boolean tradeCal(String start_date) {
        List<TradeDateVo> tradeDateVos = this.tradeCal("SSE", start_date, start_date);
        if(!CollectionUtils.isEmpty(tradeDateVos)){
            TradeDateVo tradeDateVo = tradeDateVos.get(0);
            return tradeDateVo != null && tradeDateVo.getIsOpen().equals("1");
        }
        return false;
    }

    /**
     * 获取 start_date 到 end_date 的数据
     * @param ts_code
     * @param start_date
     * @param end_date
     * @return
     */
    @Override
    public List<DailyVo> daily(String ts_code, String start_date, String end_date) {
        Query query = new Query(
                Criteria.where("ts_code").is(ts_code)
                        .andOperator(
                                Criteria.where("trade_date").gte(start_date),
                                Criteria.where("trade_date").lte(end_date) ));
        query.with(Sort.by( Sort.Order.desc("trade_date") ));
        List<DailyVo> dailys = mongoTemplate.find(query, DailyVo.class, "daily");
        return dailys;
    }

    /**
     * 获取 (date - back_day) 到 (date - 1) 的所有数据
     * @param ts_code
     * @param date
     * @param back_day
     * @return
     */
    @Override
    public List<DailyVo> daily(String ts_code, String date, int back_day) {
        LocalDate startDateL = LocalDate.parse(date, TimeUtil.SHORT_DATE_FORMATTER).minus(back_day, ChronoUnit.DAYS );
        LocalDate endDateL = LocalDate.parse(date, TimeUtil.SHORT_DATE_FORMATTER).minus(1, ChronoUnit.DAYS );
        List<DailyVo> data = this.daily(ts_code,startDateL.format(TimeUtil.SHORT_DATE_FORMATTER),  endDateL.format(TimeUtil.SHORT_DATE_FORMATTER));
        return data;
    }
}

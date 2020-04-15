package com.trade.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.trade.config.IndexMarketConstantConfig;
import com.trade.service.common.DataService;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.IndexBasicVo;
import com.trade.vo.StockBasicVo;
import com.trade.vo.TradeDateVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author georgy
 * @Date 2020-01-10 下午 3:09
 * @DESC TODO
 */
@Service
@CacheConfig(cacheNames = {"DataService"})
public class DataServiceImpl implements DataService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${message.url}")
    private String BASE_URL;
    @Value("${message.service.daily}")
    private String DAILY;
    @Value("${message.service.trade_cal}")
    private String TRADE_CAL;
    @Value("${message.service.stock_basic}")
    private String STOCK_BASIC;
    @Value("${message.service.index_basic}")
    private String INDEX_BASIC;

    @Autowired
    private IndexMarketConstantConfig indexMarketConstantConfig;

    /**
     * 股票列表
     * @return
     */
    @Override
    @Cacheable(key = "'stock_basic'")
    public List<StockBasicVo> stock_basic(){
        String url = BASE_URL + STOCK_BASIC;
        String response = restTemplate.getForObject(url, String.class);
        List<StockBasicVo> data = JSON.parseObject(response, new TypeReference<List<StockBasicVo>>(){});
        return data;
    }

    /**
     * 指数列表
     * @return
     */
    @Override
    @Cacheable(key = "'index_basic'")
    public List<IndexBasicVo> index_basic(){
        List<IndexBasicVo> datas = new ArrayList<>();
        indexMarketConstantConfig.getIndex_basic_markets().forEach((k, v) ->{
            String url = BASE_URL + INDEX_BASIC
                    .replaceAll("<market>", k);
            String response = restTemplate.getForObject(url, String.class);
            List<IndexBasicVo> data = JSON.parseObject(response, new TypeReference<List<IndexBasicVo>>(){});
            datas.addAll(data);
        });

        return datas;
    }

    /**
     * 交易日历
     * @param start_date
     * @return
     */
    @Override
    @Cacheable(key = "'tradeCal-'+#exchange+'-'+#start_date+'-'+#end_date")
    public List<TradeDateVo> tradeCal(String exchange, String start_date, String end_date){
        String url = BASE_URL + TRADE_CAL
                .replaceAll("<exchange>", exchange)
                .replaceAll("<start_date>", start_date)
                .replaceAll("<end_date>", end_date);
        String response = restTemplate.getForObject(url, String.class);
        List<TradeDateVo> data = JSON.parseObject(response, new TypeReference<List<TradeDateVo>>(){});
        return data;
    }

    /**
     * 交易日历 - 判断当前时间是否交易日
     * @param start_date
     * @return
     */
    @Override
    @Cacheable(key = "'tradeCal-'+#start_date")
    public boolean tradeCal(String start_date){
        List<TradeDateVo> tradeDateVos = this.tradeCal("SSE", start_date, start_date);
        if(!CollectionUtils.isEmpty(tradeDateVos)){
            TradeDateVo tradeDateVo = tradeDateVos.get(0);
            return tradeDateVo != null && tradeDateVo.getIsOpen().equals("1");
        }
        return false;
    }

    /**
     * 日线行情
     * @param ts_code
     * @param start_date
     * @param end_date
     * @return
     */
    @Override
    @Cacheable(key = "'daily-'+#ts_code+'-'+#start_date+'-'+#end_date")
    public List<DailyVo> daily(String ts_code, String start_date, String end_date){
        // 由于每分钟最多调用60次，所以控制一下
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String url = BASE_URL + DAILY
                .replaceAll("<ts_code>", ts_code)
                .replaceAll("<start_date>", start_date)
                .replaceAll("<end_date>", end_date);
        String response = restTemplate.getForObject(url, String.class);

        List<DailyVo> data = null;
        if(StringUtils.isNotBlank(response)){
            data = JSON.parseObject(response, new TypeReference<List<DailyVo>>(){});
        }

        return data;
    }

    /**
     * 日线行情 - 获取当前时间往后M个交易日的数据 -> 扩大查询范围，之后获取前 back_day 条数据
     * @param ts_code
     * @param start_date
     * @param back_day 往后几天
     * @return
     */
    @Override
    @Cacheable(key = "'daily-'+#ts_code+'-'+#start_date+'-'+#back_day")
    public List<DailyVo> daily(String ts_code, String start_date, int back_day){
        int limit = back_day > 15? back_day: 15; // 扩大梯度
        LocalDate startDateL = LocalDate.parse(start_date, TimeUtil.SHORT_DATE_FORMATTER).minus(back_day + limit, ChronoUnit.DAYS );
        LocalDate endDateL = LocalDate.parse(start_date, TimeUtil.SHORT_DATE_FORMATTER).minus(1, ChronoUnit.DAYS );
        List<DailyVo> data = this.daily(ts_code,startDateL.format(TimeUtil.SHORT_DATE_FORMATTER),  endDateL.format(TimeUtil.SHORT_DATE_FORMATTER));
        return data.subList(0, back_day);
    }

}

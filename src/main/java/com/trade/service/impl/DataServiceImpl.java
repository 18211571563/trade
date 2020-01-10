package com.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.trade.service.DataService;
import com.trade.vo.DailyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-10 下午 3:09
 * @DESC TODO
 */
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${message.url}")
    private String BASE_URL;
    @Value("${message.service.daily}")
    private String DAILY;

    /**
     * 日线行情
     * @param ts_code
     * @param start_date
     * @param end_date
     * @return
     */
    @Override
    public List<DailyVo> daily(String ts_code, String start_date, String end_date){
        String url = BASE_URL + DAILY
                .replaceAll("<ts_code>", ts_code)
                .replaceAll("<start_date>", start_date)
                .replaceAll("<end_date>", end_date);
        String response = restTemplate.getForObject(url, String.class);
        List<DailyVo> data = JSON.parseObject(response, new TypeReference<List<DailyVo>>(){});
        return data;
    }

}

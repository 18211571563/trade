package com.trade.service.common.impl;

import com.BaseTest;
import com.alibaba.fastjson.JSON;
import com.trade.service.common.DataService;
import com.trade.vo.DailyVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.junit.Assert.*;

public class DataServiceImplTest extends BaseTest {

    @Autowired
    @Qualifier("dataServiceImpl")
    private DataService dataService;

    @Test
    public void stock_basic() {
    }

    @Test
    public void index_basic() {
    }

    @Test
    public void tradeCal() {
    }

    @Test
    public void tradeCal1() {
    }

    @Test
    public void daily() {
    }

    @Test
    public void daily1() {
        List<DailyVo> daily = dataService.daily("000001.SH", "20200414", 11);
        log.info(JSON.toJSONString(daily));
    }
}
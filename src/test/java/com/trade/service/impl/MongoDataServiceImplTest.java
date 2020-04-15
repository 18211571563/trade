package com.trade.service.impl;

import com.BaseTest;
import com.trade.service.common.DataService;
import com.trade.vo.IndexBasicVo;
import com.trade.vo.StockBasicVo;
import com.trade.vo.TradeDateVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class MongoDataServiceImplTest extends BaseTest {

    @Autowired
    private DataService dataService;

    @Test
    public void stock_basic() {
        List<StockBasicVo> stockBasicVos = dataService.stock_basic();
        System.out.println(stockBasicVos.size());
    }

    @Test
    public void index_basic() {
        List<IndexBasicVo> indexBasicVos = dataService.index_basic();
        System.out.println(indexBasicVos.size());
    }

    @Test
    public void tradeCal() {
        List<TradeDateVo> sse = dataService.tradeCal("SSE", "20100305", "20190801");
        System.out.println(sse.get(0).getCalDate() + " - " + sse.get(sse.size() - 1).getCalDate());
    }

    @Test
    public void tradeCal1() {
    }

    @Test
    public void daily() {
    }

    @Test
    public void daily1() {
    }
}
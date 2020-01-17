package com.trade.job;

import com.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class SynDataJobTest extends BaseTest {

    @Autowired
    private SynDataJob synDataJob;

    /**
     * 同步日历
     */
    @Test
    public void tradeCalSym() {
        synDataJob.tradeCalSym("20100101", "20200117");
    }

    @Test
    public void dailyOnlySym() {
        synDataJob.dailyOnlySym("20100101", "20200117");
    }

    /**
     * 同步基础数据
     */
    @Test
    public void stockBasicSym() {
        synDataJob.stockBasicSym();
    }

}
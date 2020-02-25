package com.trade.job;

import com.BaseTest;
import com.trade.utils.TimeUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class SynDataJobTest extends BaseTest {

    @Autowired
    private SynDataJob synDataJob;

    /**
     * 同步日历
     */
    @Test
    public void tradeCalSym() {
        synDataJob.tradeCalSym();
    }

    @Test
    public void dailyOnlySym() {
        synDataJob.dailyOnlySym("20200224", LocalDate.now().minus(1, ChronoUnit.DAYS).format(TimeUtil.SHORT_DATE_FORMATTER));
    }

    /**
     * 同步基础数据
     */
    @Test
    public void stockBasicSym() {
        synDataJob.stockBasicSym();
    }

}
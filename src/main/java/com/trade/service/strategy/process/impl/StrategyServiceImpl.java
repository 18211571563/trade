package com.trade.service.strategy.process.impl;

import com.alibaba.fastjson.JSON;
import com.trade.capital.CapitalManager;
import com.trade.config.TradeConstantConfig;
import com.trade.memory_storage.MemoryStorage;
import com.trade.service.common.DataService;
import com.trade.service.common.MemoryService;
import com.trade.service.common.RecordTradeMessageService;
import com.trade.service.common.TradeService;
import com.trade.service.strategy.close.CloseStrategyService;
import com.trade.service.strategy.open.OpenStrategyService;
import com.trade.service.strategy.process.StrategyService;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderVo;
import com.trade.vo.StockBasicVo;
import com.trade.vo.TradeDateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Author georgy
 * @Date 2020-01-09 下午 4:32
 * @DESC 策略服务
 */
@Service
public class StrategyServiceImpl implements StrategyService {


    private int threadCount;
    private String[] tsCodes;
    private Boolean all;
    private int unit;
    private String today;
    private String startDate;
    private String endDate;
    private int atrPeriod;
    private int breakOpenDay;
    private int breakCloseDay;
    private int filterDay;
    private String openStrategyCode;
    private String closeStrategyCode;
    private int offset;

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger assetLogger = LoggerFactory.getLogger("asset");
    private Logger capitalLogger = LoggerFactory.getLogger("capital");

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private DataService dataService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private CapitalManager capitalManager;
    @Autowired
    private OpenStrategyService openStrategyService;
    @Autowired
    private CloseStrategyService closeStrategyService;
    @Autowired
    private RecordTradeMessageService recordTradeMessageService;

    /** ################################################### public ########################################################################################## **/

    /**
     * 初始化配置 + 启动多线程执行任务
     * @throws InterruptedException
     */
    @Override
    public String exec() throws InterruptedException {
        // 初始化参数
        this.init();

        // 启动
        this.process();

        // 初始化资金管理 - 还原
        capitalManager.init();

        return MDC.get("traceId");
    }

    /** ################################################### private ########################################################################################## **/

    /**
     * 启动多线程运行任务
     * @throws InterruptedException
     */
    private void process() throws InterruptedException {

        // 获取当前的traceId
        String traceId = MDC.get("traceId");

        // 转换时间格式
        LocalDate startDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER);
        LocalDate endDateL = LocalDate.parse(endDate, TimeUtil.SHORT_DATE_FORMATTER);
        LocalDate dateL = startDateL;

        for(; endDateL.compareTo(dateL) >= 0; dateL = dateL.plusDays(1)){
            String date = dateL.format(TimeUtil.SHORT_DATE_FORMATTER);
            if(dataService.tradeCal(date)){

                logger.info("********** 新的一天:{} **********", date);
                assetLogger.info("********** 新的一天:{} **********", date);
                capitalLogger.info("********** 新的一天:{} **********", date);

                // 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
//                ExecutorService executor = Executors.newFixedThreadPool(threadCount);

                for (String tsCode : tsCodes) {
//                    executor.execute(() -> {
                        // 设置本地线程MDC
                        MDC.put("traceId", traceId);
                        MDC.put("tsCode", tsCode);
                        this.process(tsCode, date);

//                    });
                }

//                executor.shutdown();
//                while(true){
//                    if(executor.isTerminated()){
                        logger.info("*******************************\r\n");
                        assetLogger.info("*******************************\r\n");
                        capitalLogger.info("*******************************\r\n");
//                        break;
//                    }
//                    Thread.sleep(200);
//                }

            }else{
                logger.warn("非交易日:{}", date);
            }
        }


        // 统计交易记录
        for (String tsCode : tsCodes) {
            recordTradeMessageService.statistics(tsCode);
        }
        // 统计资金信息
        recordTradeMessageService.statisticsCapital();
        logger.info("所有任务执行完成！");

    }

    /**
     * 执行 标的 + 某一天 任务
     * @param tsCode
     * @param date
     */
    private void process(String tsCode, String date){

        /************************************************************** 获取今日行情 ***********************************************************************/
        List<DailyVo> dailys = dataService.daily(tsCode, date, date);
        if(dailys == null || dailys.size() == 0) {
            logger.warn("获取不到数据, tsCode: {}, date: {}", tsCode, date);
            return;
        }
        DailyVo daily = dailys.get(0);

        /************************************************************** 获取仓位信息 ***********************************************************************/
        OrderVo orderVo = tradeService.getOrderVo(tsCode);

        /***************************************************************** 开仓 ************************************************************************/
        openStrategyService.open(daily, orderVo,  openStrategyCode);

        /***************************************************************** 止损 ************************************************************************/
        closeStrategyService.close(daily, orderVo, closeStrategyCode);

        /***************************************************************** 滤镜 ************************************************************************/
        // 滤镜: 判断当前的开仓信号是否与长期趋势背离，如背离，终止交易


    }


    /**
     * 初始化
     */
    private void init() {
        this.initConfig(); // 默认读取配置文件
        if(this.all) this.initAllTsCodes(); // 初始化所有标的到选样池

    }

    /**
     * 初始化配置 - 读取配置文件
     */
    private void initConfig(){
        /** 初始化资金管理 **/
        capitalManager.init();

        /** 初始化参数 **/
        this.openStrategyCode = tradeConstantConfig.getOpenStrategyCode();
        this.closeStrategyCode = tradeConstantConfig.getCloseStrategyCode();
        this.tsCodes = tradeConstantConfig.getTsCodes();
        this.all = tradeConstantConfig.getUsedAll();
        this.unit = tradeConstantConfig.getUnit();
        this.today = tradeConstantConfig.getToday();
        this.startDate = tradeConstantConfig.getStartDate();
        this.endDate = tradeConstantConfig.getEndDate();

        this.atrPeriod = tradeConstantConfig.getAtrPeriod();
        this.breakOpenDay = tradeConstantConfig.getBreakOpenDay();
        this.breakCloseDay = tradeConstantConfig.getBreakCloseDay();
        this.filterDay = tradeConstantConfig.getFilterDay();
        this.offset = tradeConstantConfig.getOffset();
        this.threadCount = tradeConstantConfig.getThreadCount();

    }

    /**
     * 初始化所有标的到选样池
     */
    private void initAllTsCodes() {
        List<StockBasicVo> stockBasicVos = dataService.stock_basic();
        List<String> initTsCodes = new ArrayList<>();
        stockBasicVos.forEach(stockBasicVo -> {
            String ts_code = stockBasicVo.getTs_code();
            initTsCodes.add(ts_code);
        });
        tsCodes = initTsCodes.toArray(new String[initTsCodes.size()]);
    }



}



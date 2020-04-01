package com.trade.service.strategy;

import com.alibaba.fastjson.JSON;
import com.trade.capital.CapitalManager;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.DataService;
import com.trade.service.common.TradeService;
import com.trade.service.strategy.close.CloseStrategyService;
import com.trade.service.strategy.open.OpenStrategyService;
import com.trade.service.strategy.StrategyService;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.OrderBPVo;
import com.trade.vo.OrderVo;
import com.trade.vo.StockBasicVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author georgy
 * @Date 2020-01-09 下午 4:32
 * @DESC 策略服务
 */
@Service
public class StrategyServiceImpl implements StrategyService {

    /** 线程数量 **/
    @Value("${trade.constant.threadCount}")
    private int threadCount;

    private String[] tsCodes;
    private Boolean all;
    private Boolean isUsedCapitail;
    private int unit;
    private String today;
    private String startDate;
    private String endDate;
    private int atrPeriod;
    private int breakOpenDay;
    private int breakCloseDay;
    private int filterDay;

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger assetLogger = LoggerFactory.getLogger("asset");

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

    /**
     * 初始化启动项
     */
    private void init(){
        /** 初始化资金管理 **/
        capitalManager.init();

        /** 初始化参数 **/
        this.tsCodes = tradeConstantConfig.getTsCodes();
        this.all = tradeConstantConfig.getUsedAll();
        this.isUsedCapitail = tradeConstantConfig.getUsedCapitail();
        this.unit = tradeConstantConfig.getUnit();
        this.today = tradeConstantConfig.getToday();
        this.startDate = tradeConstantConfig.getStartDate();
        this.endDate = tradeConstantConfig.getEndDate();

        this.atrPeriod = tradeConstantConfig.getAtrPeriod();
        this.breakOpenDay = tradeConstantConfig.getBreakOpenDay();
        this.breakCloseDay = tradeConstantConfig.getBreakCloseDay();
        this.filterDay = tradeConstantConfig.getFilterDay();
    }

    /**
     * 初始化配置 + 启动多线程执行任务
     * @throws InterruptedException
     */
    @Override
    public void process(String startDate, String endDate, String today, Boolean all, String tsCodes ) throws InterruptedException {
        // 初始化参数
        this.init(); // 默认读取配置文件
        if(StringUtils.isNotBlank(startDate)) this.startDate = startDate;
        if(StringUtils.isNotBlank(endDate)) this.endDate = endDate;
        if(StringUtils.isNotBlank(today)) this.today = today;
        if(all != null) this.all = all;
        if(StringUtils.isNotBlank(tsCodes)) this.tsCodes = tsCodes.split(",");
        process();

    }

    /**
     * 初始化配置 + 启动多线程执行任务
     * @throws InterruptedException
     */
    @Override
    public void process(String startDate, String endDate, String today) throws InterruptedException {
        // 初始化参数
        this.init(); // 默认读取配置文件
        if(StringUtils.isNotBlank(startDate)) this.startDate = startDate;
        if(StringUtils.isNotBlank(endDate)) this.endDate = endDate;
        if(StringUtils.isNotBlank(today)) this.today = today;
        process();

    }

    /**
     * 启动多线程运行任务
     * @throws InterruptedException
     */
    private void process() throws InterruptedException {
        // 获取 选样池信息
        if(all){
            List<StockBasicVo> stockBasicVos = dataService.stock_basic();
            List<String> initTsCodes = new ArrayList<>();
            stockBasicVos.forEach(stockBasicVo -> {
                String ts_code = stockBasicVo.getTs_code();
                initTsCodes.add(ts_code);
            });
            tsCodes = initTsCodes.toArray(new String[initTsCodes.size()]);
        }


        ExecutorService executor = Executors.newFixedThreadPool(threadCount); // 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
        for (String tsCode : tsCodes) {
            executor.execute(() -> {
                MDC.put("tsCode", tsCode);
                this.process(tsCode);
            });
        }

        executor .shutdown();
        while(true){
            if(executor.isTerminated()){
                System.out.println("所有任务执行完成！");
                break;
            }
            Thread.sleep(1000);
        }
    }

    /**
     * 执行 标的 + 所有时间 任务
     * @param tsCode
     */
    private void process(String tsCode){
        /***************************************************************** for *********************************************************************/
        LocalDate startDateL = LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER);
        LocalDate endDateL = LocalDate.parse(endDate, TimeUtil.SHORT_DATE_FORMATTER);
        LocalDate dateL = startDateL;
        for(int i = 0; endDateL.compareTo(dateL) >= 0; dateL = dateL.plusDays(1)){
            String date = dateL.format(TimeUtil.SHORT_DATE_FORMATTER);
            if(dataService.tradeCal(date)){
                logger.info("********** 新的一天:{} **********", date);
                this.process(tsCode, date);
                logger.info("*******************************\r\n");
            }else{
                logger.warn("非交易日:{}", date);
            }
        }
        assetLogger.info("########################### {} ###############################", tsCodes);
        assetLogger.info("总资金:{}, 可用资金:{}, 冻结资金: {}, 风险系数:{}",CapitalManager.assetVo.getTotalCapital(), CapitalManager.assetVo.getUsableCapital(), CapitalManager.assetVo.getFrozenCapital(), CapitalManager.assetVo.getRiskParameter());
        List<OrderBPVo> orderBPVos = CapitalManager.tradeOrdersHistoryMap.get(tsCode);
        for (OrderBPVo orderBPVo : orderBPVos) {
            assetLogger.info("标的:{}, 方向:{}, bp:{}, bp比率:{}, 开仓价:{}, 止损价:{}, 交易量:{}, 交易时间:{} ",
                    orderBPVo.getTsCode(), orderBPVo.getDirection(), orderBPVo.getBp(), orderBPVo.getBpRate(), orderBPVo.getOpen(), orderBPVo.getClose(), orderBPVo.getVolume(), orderBPVo.getTradeDate());
        }
        assetLogger.info("--------------------------- {} -------------------------------", tsCodes);
    }

    /**
     * 执行 标的 + 某一天 任务
     * @param tsCode
     * @param date
     */
    private void process(String tsCode, String date){

        // 获取今日行情
        DailyVo daily = dataService.daily(tsCode, date, date).get(0);
        // 获取仓位信息
        OrderVo orderVo = tradeService.getOrderVo(tsCode);

        /***************************************************************** 开仓 ************************************************************************/
        openStrategyService.breakOpen(daily, orderVo);

        /***************************************************************** 止损 ************************************************************************/
        closeStrategyService.breakClose(daily, orderVo);

        /***************************************************************** 滤镜 ************************************************************************/
        // 滤镜: 判断当前的开仓信号是否与长期趋势背离，如背离，终止交易



    }



}



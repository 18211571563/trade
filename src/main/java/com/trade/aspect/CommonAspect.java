package com.trade.aspect;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.memory_storage.MemoryStorage;
import com.trade.service.common.DataService;
import com.trade.utils.TimeUtil;
import com.trade.vo.DailyVo;
import com.trade.vo.TradeDateVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by georgy on 2018/11/26.
 */
@Aspect
@Component
public class CommonAspect {

    public static Boolean process = false;

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Qualifier("mongoDataServiceImpl")
    private DataService mongodbDataService;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 策略入口切面
     * @param joinPoint
     * @return
     */
    @Around(value="execution(public * com.trade.service.strategy.process.impl.StrategyServiceImpl.exec())")
    public Object processPre(ProceedingJoinPoint joinPoint) throws Throwable {
        process = true;
        Date date = new Date();
        Object result = null;
        try {
            result = joinPoint.proceed();
        }finally {
            process = false;
        }
        logger.info(String.format("总耗时：%s毫秒", String.valueOf((new Date().getTime() - date.getTime()) )) );
        return result;
    }

//    /**
//     * 策略每个标的初始化切面 - 初始化本地线程数据
//     * @param joinPoint
//     * @return
//     */
//    @Around(value="execution(public void com.trade.service.strategy.process.impl.StrategyServiceImpl.process(java.lang.String, java.lang.String, java.lang.String))")
//    public Object processTsCodePre(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object[] args = joinPoint.getArgs();
//        String tsCode = String.valueOf(args[0]);
//        String startDate = String.valueOf(args[1]);
//        String endDate = String.valueOf(args[2]);
//
//        // 初始化 MemoryStorage 到本地线程
//        ThreadLocal<MemoryStorage> memoryStorageThreadLocal = MemoryStorage.memoryStorageThreadLocal;
//        MemoryStorage memoryStorage = new MemoryStorage();
//
//        List<DailyVo> daily = mongodbDataService.daily( tsCode,
//                LocalDate.parse(startDate, TimeUtil.SHORT_DATE_FORMATTER).minus(tradeConstantConfig.getOffset() + 30, ChronoUnit.DAYS ).format(TimeUtil.SHORT_DATE_FORMATTER),
//                endDate);
//        HashMap<String, List<DailyVo>> dailyVoMaps = new HashMap<>();
//        dailyVoMaps.put(tsCode, daily);
//        memoryStorage.setDailyVoMaps(dailyVoMaps);
//        memoryStorage.setDailyTradeDateList(daily.stream().map(a -> a.getTrade_date()).collect(Collectors.toList()));
//
//        List<TradeDateVo> tradeDateVos = mongodbDataService.tradeCal("SSE", startDate, endDate);
//        memoryStorage.setTradeDateVoList(tradeDateVos);
//
//        memoryStorageThreadLocal.set(memoryStorage);
//
//        MDC.put("traceId", MDC.get("traceId"));
//        MDC.put("tsCode", tsCode);
//
//        Object result = joinPoint.proceed();
//
//        // 执行完成清理 MemoryStorage
//        memoryStorageThreadLocal.remove();
//        return result;
//    }



}

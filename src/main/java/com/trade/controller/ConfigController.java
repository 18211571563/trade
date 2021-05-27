package com.trade.controller;

import com.alibaba.fastjson.JSON;
import com.trade.config.TradeConstantConfig;
import com.trade.service.common.MemoryService;
import com.trade.service.strategy.process.StrategyService;
import com.trade.utils.CommonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author georgy
 * @Date 2020-07-21 下午 1:50
 * @DESC TODO
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private TradeConstantConfig tradeConstantConfig;
    @Autowired
    private MemoryService memoryService;

    @GetMapping(value = "/update")
    public String updateConfig(TradeConstantConfig config) {
        BeanUtils.copyProperties(config, tradeConstantConfig, CommonUtil.getNullPropertyNames(config));
        return "success";
    }

    @GetMapping(value = "/get")
    public String getConfig() throws InvocationTargetException, IllegalAccessException {
        return JSON.toJSONString(tradeConstantConfig);
    }

}

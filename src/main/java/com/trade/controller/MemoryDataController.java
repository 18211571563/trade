package com.trade.controller;

import com.trade.ResourceManager.ThreadPoolManager;
import com.trade.aspect.CommonAspect;
import com.trade.service.common.DataService;
import com.trade.service.common.MemoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author georgy
 * @Date 2020-09-22 下午 8:38
 * @DESC TODO
 */
@RestController
@RequestMapping("/")
public class MemoryDataController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MemoryService memoryService;

    @GetMapping(value = "load")
    public String load(){
        if(CommonAspect.load) throw new RuntimeException("数据加载中，请莫清除！");
        if(CommonAspect.process) throw new RuntimeException("策略运行中，请莫清除！");
        memoryService.clear();
        memoryService.asyncLoad();
        return "success";
    }

    @GetMapping(value = "clear")
    public String clear(){
        if(CommonAspect.load) throw new RuntimeException("数据加载中，请莫清除！");
        if(CommonAspect.process) throw new RuntimeException("策略运行中，请莫清除！");
        memoryService.clear();
        return "success";
    }

}

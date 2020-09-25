package com.trade.service.common;

/**
 * @Author georgy
 * @Date 2020-09-22 下午 8:43
 * @DESC TODO
 */
public interface MemoryService {

    /**
     * 加载数据
     */
    public void load();

    /**
     * 异步加载
     */
    public void asyncLoad();

    /**
     * 清除缓存
     */
    public void clear();


}

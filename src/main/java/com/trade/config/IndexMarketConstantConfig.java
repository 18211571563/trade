package com.trade.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author georgy
 * @Date 2020-04-15 下午 2:25
 * @DESC 交易指数
 */
@Component
@ConfigurationProperties(prefix = "trade")
public class IndexMarketConstantConfig {

    /**
     * 交易指数 - 市场说明(market)
     */
    private Map<String, String> index_basic_markets;

    /**
     * 使用的指数代码 -> 代码太多，只选择需要的
     */
    private Map<String, String> used_index_basic_tsCodes;


    public Map<String, String> getIndex_basic_markets() {
        return index_basic_markets;
    }

    public void setIndex_basic_markets(Map<String, String> index_basic_markets) {
        this.index_basic_markets = index_basic_markets;
    }

    public Map<String, String> getUsed_index_basic_tsCodes() {
        return used_index_basic_tsCodes;
    }

    public void setUsed_index_basic_tsCodes(Map<String, String> used_index_basic_tsCodes) {
        this.used_index_basic_tsCodes = used_index_basic_tsCodes;
    }
}

package com.trade.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author georgy
 * @Date 2020-04-15 下午 5:53
 * @DESC 策略常量
 */
@Component
@ConfigurationProperties(prefix = "trade.strategy")
public class StrategyConstantConfig {

    /**
     * 策略编码 - 开仓编码集合
     */
    private Map<String, String> open_codes;

    /**
     * 策略编码 - 止损编码集合
     */
    private Map<String, String> close_codes;

    public Map<String, String> getOpen_codes() {
        return open_codes;
    }

    public void setOpen_codes(Map<String, String> open_codes) {
        this.open_codes = open_codes;
    }

    public Map<String, String> getClose_codes() {
        return close_codes;
    }

    public void setClose_codes(Map<String, String> close_codes) {
        this.close_codes = close_codes;
    }
}

package com.trade.config.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;

import java.util.concurrent.TimeUnit;

/**
 * @author gaodp
 */
public class HttpClientProperties {
    private final ConnectionConfig.Builder defaultConnectionConfig = ConnectionConfig.custom();
    private final RequestConfig.Builder defaultRequestConfig = RequestConfig.custom();
    private final SocketConfig.Builder defaultSocketConfig = SocketConfig.custom();

    private boolean evictExpiredConnections;
    private boolean evictIdleConnections;
    private long maxIdleTime;
    private TimeUnit maxIdleTimeUnit;

    private boolean systemProperties;
    private boolean redirectHandlingDisabled;
    private boolean automaticRetriesDisabled;
    private boolean contentCompressionDisabled;
    private boolean cookieManagementDisabled;
    private boolean authCachingDisabled;
    private boolean connectionStateDisabled;

    private int maxConnTotal = 0;
    private int maxConnPerRoute = 0;

    private long connTimeToLive = -1;
    private TimeUnit connTimeToLiveTimeUnit = TimeUnit.MILLISECONDS;

    public ConnectionConfig.Builder getDefaultConnectionConfig() {
        return defaultConnectionConfig;
    }

    public RequestConfig.Builder getDefaultRequestConfig() {
        return defaultRequestConfig;
    }

    public SocketConfig.Builder getDefaultSocketConfig() {
        return defaultSocketConfig;
    }

    public boolean isEvictExpiredConnections() {
        return evictExpiredConnections;
    }

    public void setEvictExpiredConnections(boolean evictExpiredConnections) {
        this.evictExpiredConnections = evictExpiredConnections;
    }

    public boolean isEvictIdleConnections() {
        return evictIdleConnections;
    }

    public void setEvictIdleConnections(boolean evictIdleConnections) {
        this.evictIdleConnections = evictIdleConnections;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public TimeUnit getMaxIdleTimeUnit() {
        return maxIdleTimeUnit;
    }

    public void setMaxIdleTimeUnit(TimeUnit maxIdleTimeUnit) {
        this.maxIdleTimeUnit = maxIdleTimeUnit;
    }

    public boolean getSystemProperties() {
        return systemProperties;
    }

    public void setSystemProperties(boolean systemProperties) {
        this.systemProperties = systemProperties;
    }

    public boolean isRedirectHandlingDisabled() {
        return redirectHandlingDisabled;
    }

    public void setRedirectHandlingDisabled(boolean redirectHandlingDisabled) {
        this.redirectHandlingDisabled = redirectHandlingDisabled;
    }

    public boolean isAutomaticRetriesDisabled() {
        return automaticRetriesDisabled;
    }

    public void setAutomaticRetriesDisabled(boolean automaticRetriesDisabled) {
        this.automaticRetriesDisabled = automaticRetriesDisabled;
    }

    public boolean isContentCompressionDisabled() {
        return contentCompressionDisabled;
    }

    public void setContentCompressionDisabled(boolean contentCompressionDisabled) {
        this.contentCompressionDisabled = contentCompressionDisabled;
    }

    public boolean isCookieManagementDisabled() {
        return cookieManagementDisabled;
    }

    public void setCookieManagementDisabled(boolean cookieManagementDisabled) {
        this.cookieManagementDisabled = cookieManagementDisabled;
    }

    public boolean isAuthCachingDisabled() {
        return authCachingDisabled;
    }

    public void setAuthCachingDisabled(boolean authCachingDisabled) {
        this.authCachingDisabled = authCachingDisabled;
    }

    public boolean isConnectionStateDisabled() {
        return connectionStateDisabled;
    }

    public void setConnectionStateDisabled(boolean connectionStateDisabled) {
        this.connectionStateDisabled = connectionStateDisabled;
    }

    public int getMaxConnTotal() {
        return maxConnTotal;
    }

    public void setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }

    public int getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public long getConnTimeToLive() {
        return connTimeToLive;
    }

    public void setConnTimeToLive(long connTimeToLive) {
        this.connTimeToLive = connTimeToLive;
    }

    public TimeUnit getConnTimeToLiveTimeUnit() {
        return connTimeToLiveTimeUnit;
    }

    public void setConnTimeToLiveTimeUnit(TimeUnit connTimeToLiveTimeUnit) {
        this.connTimeToLiveTimeUnit = connTimeToLiveTimeUnit;
    }
}

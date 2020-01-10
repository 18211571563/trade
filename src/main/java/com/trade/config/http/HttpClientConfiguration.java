package com.trade.config.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;


/**
 * 应用内统一使用{@link HttpClient}工具
 * @author gaodp
 */
@ConditionalOnClass(HttpClient.class)
@Configuration
public class HttpClientConfiguration {

    @Autowired
    private HttpClientBuilder httpClientBuilder;
    @Autowired
    private HttpClientRequestInterceptor httpClientRequestInterceptor;

    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(@Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager){
        //HttpClientBuilder中的构造方法被protected修饰，所以这里不能直接使用new来实例化一个HttpClientBuilder，可以使用HttpClientBuilder提供的静态方法create()来获取HttpClientBuilder对象
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
        return httpClientBuilder;
    }

    /**     * 首先实例化一个连接池管理器，设置最大连接数、并发连接数     * @return     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getHttpClientConnectionManager(){
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();        //最大连接数
        httpClientConnectionManager.setMaxTotal(100);        //并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(100);
        return httpClientConnectionManager;
    }


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate(httpComponentsClientHttpRequestFactory());
        // 使用UTF-8编码
        for (HttpMessageConverter<?> messageConverter : template.getMessageConverters()) {
            if (messageConverter instanceof AbstractHttpMessageConverter) {
                ((AbstractHttpMessageConverter<?>) messageConverter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
        return template;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public CloseableHttpClient httpClient() {
        HttpClientProperties properties = httpClientProperties();
        HttpClientBuilder builder = this.httpClientBuilder;
        if (properties.isEvictExpiredConnections()) {
            builder.evictExpiredConnections();
        }
        if (properties.isEvictIdleConnections()) {
            builder.evictIdleConnections(properties.getMaxIdleTime(), properties.getMaxIdleTimeUnit());
        }
        if (properties.getSystemProperties()) {
            builder.useSystemProperties();
        }
        if (properties.isRedirectHandlingDisabled()) {
            builder.disableRedirectHandling();
        }
        if (properties.isAutomaticRetriesDisabled()) {
            builder.disableAutomaticRetries();
        }
        if (properties.isContentCompressionDisabled()) {
            builder.disableContentCompression();
        }
        if (properties.isCookieManagementDisabled()) {
            builder.disableCookieManagement();
        }
        if (properties.isAuthCachingDisabled()) {
            builder.disableAuthCaching();
        }
        if (properties.isConnectionStateDisabled()) {
            builder.disableConnectionState();
        }
        return builder.setMaxConnTotal(properties.getMaxConnTotal())
                .setMaxConnPerRoute(properties.getMaxConnPerRoute())
                .setConnectionTimeToLive(properties.getConnTimeToLive(), properties.getConnTimeToLiveTimeUnit())
                .setDefaultSocketConfig(properties.getDefaultSocketConfig().build())
                .setDefaultConnectionConfig(properties.getDefaultConnectionConfig().build())
                .setDefaultRequestConfig(properties.getDefaultRequestConfig().build())
//                .setHttpProcessor(httpClientRequestInterceptor)
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "http-client")
    public HttpClientProperties httpClientProperties() {
        return new HttpClientProperties();
    }
}

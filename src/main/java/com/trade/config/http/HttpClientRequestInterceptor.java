package com.trade.config.http;

import org.apache.http.*;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * 打印HttpClient请求与响应的参数和耗时
 *
 * @author gaodp
 */
@Component
public class HttpClientRequestInterceptor implements HttpProcessor {

    private static final ThreadLocal<Long> REQUEST_START = new ThreadLocal<>();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final Logger log = LoggerFactory.getLogger(HttpClientRequestInterceptor.class);

    @Autowired
    private RequestLoggingProperties requestLogging;

    @Override
    public void process(HttpRequest request, HttpContext context) throws IOException {
        String method, uri, contentType = null, body = null;
        while (request instanceof HttpRequestWrapper) {
            request = ((HttpRequestWrapper) request).getOriginal();
        }
        if (request.containsHeader(CONTENT_TYPE)) {
            contentType = request.getFirstHeader(CONTENT_TYPE).getValue();
        }
        RequestLine requestLine = request.getRequestLine();
        method = requestLine.getMethod();
        uri = requestLine.getUri();
        if (request instanceof HttpUriRequest) {
            HttpUriRequest uriRequest = (HttpUriRequest) request;
            method = uriRequest.getMethod();
            uri = uriRequest.getURI().toString();
        }
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity httpEntity = ((HttpEntityEnclosingRequest) request).getEntity();
            if (requestLogging.shouldLogBody(contentType)) {
                body = new BufferedReader(new InputStreamReader(httpEntity.getContent()))
                        .lines().collect(Collectors.joining());
            }
        }
        if (body != null) {
            log.info("{}: {}, body: {}", method, uri, body);
        } else {
            log.info("{}: {}, Content-Type: {}", method, uri, contentType);
        }
        REQUEST_START.set(System.currentTimeMillis());
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws IOException {
        long elapse = System.currentTimeMillis() - REQUEST_START.get();
        REQUEST_START.remove();
        int status = response.getStatusLine().getStatusCode();
        String contentType = null, body = null;
        if (response.containsHeader(CONTENT_TYPE)) {
            contentType = response.getFirstHeader(CONTENT_TYPE).getValue();
        }
        if (requestLogging.shouldLogBody(contentType)) {
            body = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                    .lines().collect(Collectors.joining());
        }
        if (body != null) {
            log.info("status: {}, body: {}, cost: {}ms", status, body, elapse);
        } else {
            log.info("status: {}, Content-Type: {}, cost: {}ms", status, contentType, elapse);
        }
    }
}

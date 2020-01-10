package com.trade.config.http;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gaodp
 */
public class RequestLoggingProperties {

    private boolean logBody = true;

    private Set<String> contentTypesShouldLoggingBody = new HashSet<>();

    public boolean isLogBody() {
        return logBody;
    }

    public void setLogBody(boolean logBody) {
        this.logBody = logBody;
    }

    public Set<String> getContentTypesShouldLoggingBody() {
        return contentTypesShouldLoggingBody;
    }

    public void setContentTypesShouldLoggingBody(Set<String> contentTypesShouldLoggingBody) {
        this.contentTypesShouldLoggingBody = contentTypesShouldLoggingBody;
    }

    public boolean shouldLogBody(String contentType) {
        return contentType == null || contentType.isEmpty()
                || contentTypesShouldLoggingBody.stream().anyMatch(contentType::contains);
    }
}

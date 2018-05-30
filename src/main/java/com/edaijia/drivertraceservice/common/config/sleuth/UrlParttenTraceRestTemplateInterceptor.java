package com.edaijia.drivertraceservice.common.config.sleuth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.ErrorParser;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanInjector;
import org.springframework.cloud.sleuth.instrument.web.HttpTraceKeysInjector;
import org.springframework.cloud.sleuth.instrument.web.client.TraceRestTemplateInterceptor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author zhoutao
 * @Description: sleuth的TraceRestTemplateInterceptor是只要经过restTemplate发送的请求都会trace，
 * 包括发给spring boot admin的心跳，
 * 所以使用该过滤器过滤掉每个应用发往spring boot admin的请求，
 * 后续其他需要过滤的都可以加进来
 * @date 2018/4/20
 */
@Slf4j
@Component
public class UrlParttenTraceRestTemplateInterceptor extends TraceRestTemplateInterceptor {

    @Value("#{'${spring.sleuth.web.client.skip-uri-prefix}'.split(',')}")
    private List<String> skipUriPrefixList;

    public UrlParttenTraceRestTemplateInterceptor(Tracer tracer, HttpSpanInjector spanInjector,
                                                  HttpTraceKeysInjector httpTraceKeysInjector, ErrorParser errorParser) {
        super(tracer, spanInjector, httpTraceKeysInjector, errorParser);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String path = request.getURI().getPath();
        for (String uriPrefix : skipUriPrefixList) {
            if (path.startsWith(uriPrefix)) {
                if (log.isDebugEnabled()) {
                    log.debug("The request url {} will not track.", path);
                }
                return execution.execute(request, body);
            }
        }
        return super.intercept(request, body, execution);
    }
}

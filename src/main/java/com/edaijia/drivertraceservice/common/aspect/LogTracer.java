package com.edaijia.drivertraceservice.common.aspect;

import com.edaijia.drivertraceservice.Application;
import com.edaijia.drivertraceservice.common.utils.IPUtils;
import com.zhouyutong.zapplication.constant.SymbolConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Span;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 日志跟踪
 * 本地trace日志格式：
 * 应用名称|IP(远程或本地)|kind调用类型|name调用方或接收方|param入参|result结果|duration耗时
 * kind说明：httpReceive|httpCall|dbCall|redisCall|esCall
 * <p>
 * 远程使用sleuth
 *
 * @author zhoutao
 * @date 2018/4/24
 */
@Component
@Slf4j
public class LogTracer {
    @Value("${spring.profiles.active}")
    private String active;

    @Value("${spring.sleuth.enabled}")
    private boolean sleuthEnabled;
    @Autowired
    private org.springframework.cloud.sleuth.Tracer tracer;

    public boolean useDetailParamAndResult() {
        return "dev".equals(active) || "test".equals(active);
    }

    public void traceStart(TraceContext traceContext) {
        StringBuilder sb = traceContext.sb;
        sb.append(traceContext.fetchApplicationName()).append(SymbolConstant.BAR);
        sb.append(traceContext.fetchIp()).append(SymbolConstant.BAR);
        sb.append(traceContext.fetchKind()).append(SymbolConstant.BAR);
        sb.append(traceContext.fetchName()).append(SymbolConstant.BAR);
        sb.append(useDetailParamAndResult() ? traceContext.fetchDetailParam() : traceContext.fetchParam()).append(SymbolConstant.BAR);


        if (isSleuthEnabled(traceContext)) {
            Span newSpan = tracer.createSpan(traceContext.fetchKind());
            newSpan.tag(Span.SPAN_PEER_SERVICE_TAG_NAME, traceContext.fetchKind());
            Map<String, String> tagMap = traceContext.fetchTag();
            if (tagMap != null) {
                for (Map.Entry<String, String> entry : tagMap.entrySet()) {
                    newSpan.tag(entry.getKey(), entry.getValue());
                }
            }
            newSpan.logEvent(Span.CLIENT_SEND);
            traceContext.span = newSpan;
        }
    }

    public void traceError(Throwable throwable, TraceContext traceContext) {
        String errorMsg = traceContext.fetchError(throwable);
        traceContext.sb.append(errorMsg).append(SymbolConstant.BAR);

        if (isSleuthAppendErrorEnabled(traceContext)) {
            Span currentSpan = tracer.getCurrentSpan();
            if (currentSpan != null && currentSpan.isExportable()) {
                currentSpan.tag(Span.SPAN_ERROR_TAG_NAME, errorMsg);
            }
        }
    }

    public void traceEnd(TraceContext traceContext) {
        StringBuilder sb = traceContext.sb;
        sb.append(useDetailParamAndResult() ? traceContext.fetchDetailResult() : traceContext.fetchResult()).append(SymbolConstant.BAR);
        sb.append(traceContext.fetchDuration()).append(SymbolConstant.BAR);
        log.info(sb.toString());

        if (isSleuthEnabled(traceContext)) {
            Span newSpan = traceContext.span;
            newSpan.logEvent(Span.CLIENT_RECV);
            tracer.close(newSpan);
        }
        traceContext.clear();
    }

    /**
     * 是否可发送到sleuth
     * http类的kind由spring cloud自动装配了
     * 其他第三方需要自己发送到sleuth
     *
     * @param traceContext
     * @return
     */
    private boolean isSleuthEnabled(TraceContext traceContext) {
        String kind = traceContext.fetchKind();
        return sleuthEnabled && !"httpReceive".equals(kind) && !"httpCall".equals(kind);
    }

    /**
     * 是否可把错误发送到sleuth
     * sleuth使用TraceFilter,我们用ControllerAspect
     * 有异常后aspect自动转为resp，不在进入TraceFilter的异常处理
     * 因此接口有错误需要通过resp判断并告知sleuth
     *
     * @param traceContext
     * @return
     */
    private boolean isSleuthAppendErrorEnabled(TraceContext traceContext) {
        String kind = traceContext.fetchKind();
        return sleuthEnabled && "httpReceive".equals(kind);
    }

    public abstract static class TraceContext {
        private long start = System.currentTimeMillis();
        private StringBuilder sb = new StringBuilder();
        private Span span;

        private void clear() {
            this.sb.setLength(0);
        }

        public String currentContextString() {
            return sb.toString();
        }

        protected String fetchApplicationName() {
            return Application.NAME;
        }

        protected String fetchIp() {
            return IPUtils.LOCAL_IP;
        }

        abstract protected String fetchKind();

        abstract protected String fetchName();

        protected Map<String, String> fetchTag() {
            return null;
        }

        protected String fetchParam() {
            return SymbolConstant.EMPTY;
        }

        protected String fetchResult() {
            return SymbolConstant.EMPTY;
        }

        //详细入参
        protected String fetchDetailParam() {
            return fetchParam();
        }

        //详细结果
        protected String fetchDetailResult() {
            return fetchResult();
        }

        protected String fetchError(Throwable throwable) {
            return "Error:" + throwable.getMessage();
        }

        protected String fetchDuration() {
            long end = System.currentTimeMillis();
            return Long.toString(end - start);
        }
    }
}
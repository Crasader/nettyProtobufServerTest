package com.edaijia.drivertraceservice.common.aspect;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zhouyutong.zapplication.constant.SymbolConstant;
import com.zhouyutong.zapplication.exception.HttpCallException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 拦截restTemplate调用远程请求打印trace日志
 * 应用名称|本机IP|httpCall|远程url|入参|结果|耗时毫秒
 *
 * @author zhoutao
 * @date 2018/3/8
 */
@Aspect
@Component
@Slf4j
public class RestTemplateAspect {
    @Autowired
    private LogTracer logTracer;

    @Around("execution(public * org.springframework.web.client.RestTemplate.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        HttpCallTraceContext traceContext = new HttpCallTraceContext(pjp);
        logTracer.traceStart(traceContext);

        Object result = null;
        try {
            result = pjp.proceed();
        } catch (IllegalArgumentException ex) {
            logTracer.traceError(ex, traceContext);
            throw ex;
        } catch (Throwable ex) {
            logTracer.traceError(ex, traceContext);
            throw new HttpCallException(ex.getMessage(), ex);
        } finally {
            traceContext.setResult(result);
            logTracer.traceEnd(traceContext);
        }
        return result;
    }

    private static class HttpCallTraceContext extends LogTracer.TraceContext {
        private Object result;
        private ProceedingJoinPoint pjp;
        /**
         * 支持记录参数的restTemplate方法名
         */
        static final List<String> LOG_METHOD_LIST = Lists.newArrayList("getForObject", "postForObject", "put", "delete");
        static Joiner joiner = Joiner.on(SymbolConstant.COMMA);
        static Joiner.MapJoiner mapJoiner = Joiner.on(SymbolConstant.COMMA).withKeyValueSeparator(SymbolConstant.EQUAL);

        public HttpCallTraceContext(ProceedingJoinPoint pjp) {
            this.pjp = pjp;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        //详细入参
        @Override
        protected String fetchDetailParam() {
            //restTemplate所有get post delete put方法的请求参数都在最后一个参数
            Object[] params = pjp.getArgs();
            String methodName = pjp.getSignature().getName();
            if (LOG_METHOD_LIST.contains(methodName)) {
                Object pramObj = params[params.length - 1];
                if (pramObj instanceof Map) {
                    return mapJoiner.join((Map) pramObj);
                } else if (pramObj instanceof Object[]) {
                    return joiner.join((Object[]) pramObj);
                }
            }
            return SymbolConstant.EMPTY;
        }

        //详细结果
        @Override
        protected String fetchDetailResult() {
            if (this.result == null) {
                return SymbolConstant.EMPTY;
            }
            return this.result.toString();
        }

        @Override
        protected String fetchKind() {
            return "httpCall";
        }

        @Override
        protected String fetchName() {
            Object[] params = pjp.getArgs();
            String url = params[0].toString();
            int n = url.indexOf("?");
            if (n != -1) {
                url = url.substring(0, n - 1);
            }
            return url;
        }
    }
}

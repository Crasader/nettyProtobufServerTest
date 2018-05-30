package com.edaijia.drivertraceservice.common.aspect;

import com.google.common.base.Joiner;
import com.zhouyutong.zapplication.constant.SymbolConstant;
import com.zhouyutong.zapplication.exception.RedisCallException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 拦截RedisTemplateWarpper
 * 访问redis前检查key、打印redis访问日志
 * 应用名称|本机IP|redisCall|methodName|key名称|结果|耗时毫秒
 *
 * @author zhoutao
 * @date 2018/3/8
 */
@Aspect
@Component
@Slf4j
public class RedisTemplateAspect {
    @Autowired
    private LogTracer logTracer;

    @Around("execution(public * com.edaijia.drivertraceservice.common.config.redis.RedisTemplateWarpper.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        RedisTraceContext redisTraceContext = new RedisTraceContext(pjp);
        logTracer.traceStart(redisTraceContext);

        Object result = null;
        try {
            result = pjp.proceed();
        } catch (IllegalArgumentException ex) {
            logTracer.traceError(ex, redisTraceContext);
            throw ex;
        } catch (Throwable ex) {
            logTracer.traceError(ex, redisTraceContext);
            throw new RedisCallException(ex.getMessage(), ex);
        } finally {
            redisTraceContext.setResult(result);
            logTracer.traceEnd(redisTraceContext);
        }
        return result;
    }


    private static class RedisTraceContext extends LogTracer.TraceContext {
        private Object result;
        static Joiner joiner = Joiner.on(SymbolConstant.COMMA);
        private ProceedingJoinPoint pjp;

        public RedisTraceContext(ProceedingJoinPoint pjp) {
            this.pjp = pjp;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public String getKey() {
            Object[] params = pjp.getArgs();
            Object key = params[0];
            if (key instanceof String) {
                return key.toString();
            } else if (key instanceof Collection) {
                return joiner.join((Collection) key);
            } else if (key instanceof Map) {
                return joiner.join(((Map) key).keySet());
            }
            return SymbolConstant.EMPTY;
        }

        @Override
        protected Map<String, String> fetchTag() {
            Map<String, String> tag = new HashMap();
            tag.put("redis.op", fetchName());
            tag.put("redis.key", getKey());
            return tag;
        }

        @Override
        protected String fetchKind() {
            return "redisCall";
        }

        @Override
        protected String fetchName() {
            String redisOp = pjp.getSignature().getName();
            return redisOp;
        }
    }
}
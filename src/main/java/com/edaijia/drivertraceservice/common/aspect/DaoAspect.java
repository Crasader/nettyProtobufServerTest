package com.edaijia.drivertraceservice.common.aspect;

import com.zhouyutong.zapplication.constant.SymbolConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 拦截dao
 *
 * @author zhoutao
 * @date 2018/3/8
 */
@Aspect
@Component
public class DaoAspect {
    @Autowired
    private LogTracer logTracer;

    @Around("execution(public * com.zhouyutong.zorm.dao.AbstractBaseDao.*(..))")
    private Object around(ProceedingJoinPoint pjp) throws Throwable {
        DaoTraceContext daoTraceContext = new DaoTraceContext(pjp);
        logTracer.traceStart(daoTraceContext);
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable ex) {
            logTracer.traceError(ex, daoTraceContext);
            throw ex;
        } finally {
            daoTraceContext.setResult(result);
            logTracer.traceEnd(daoTraceContext);
        }
        return result;
    }

    private static class DaoTraceContext extends LogTracer.TraceContext {
        protected Object result;
        protected ProceedingJoinPoint pjp;

        public DaoTraceContext(ProceedingJoinPoint pjp) {
            this.pjp = pjp;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        @Override
        protected String fetchKind() {
            String c = pjp.getTarget().getClass().getSuperclass().getSimpleName();
            if (c.equals("JdbcBaseDao")) {
                return "dbCall";
            } else if (c.equals("ElasticSearchBaseDao")) {
                return "esCall";
            }
            return SymbolConstant.EMPTY;
        }

        @Override
        protected Map<String, String> fetchTag() {
            Map<String, String> tag = new HashMap();
            tag.put("db.op", fetchName());
            return tag;
        }

        @Override
        protected String fetchName() {
            String daoName = pjp.getTarget().getClass().getSimpleName();
            String daoMethodName = pjp.getSignature().getName();
            StringBuilder caller = new StringBuilder();
            caller.append(daoName).append(".").append(daoMethodName);
            return caller.toString();
        }
    }
}

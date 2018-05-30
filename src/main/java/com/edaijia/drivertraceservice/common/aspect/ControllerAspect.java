package com.edaijia.drivertraceservice.common.aspect;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zhouyutong.zapplication.api.ErrorCode;
import com.zhouyutong.zapplication.api.Resp;
import com.zhouyutong.zapplication.constant.SymbolConstant;
import com.zhouyutong.zapplication.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 接受请求时打印日志
 * 应用名称|本机IP,远程IP|httpReceive|controllerName.methodName|入参|结果|耗时毫秒
 *
 * @author zhoutao
 * @Description: 1.对接口做拦截打印调用日志<br>
 * 2.统一异常处理打印异常日志<br>
 * @date 2018/3/8
 */
@Aspect
@Component
@Slf4j
public class ControllerAspect {
    static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");
    @Autowired
    private LogTracer logTracer;


    @Around("execution(public * com.edaijia.drivertraceservice.web..*Controller.*(..))")
    public Resp around(ProceedingJoinPoint pjp) throws Throwable {
        Object[] params = pjp.getArgs();
        HttpReceiveTraceContext context = new HttpReceiveTraceContext(pjp);
        logTracer.traceStart(context);

        //请求接收先打印一个开始
        log.info(context.currentContextString() + "Start...");
        Resp resp = null;
        try {
            /**
             * jsr注解参数校验结果获取部分
             * 校验不通过直接返回结果,不走业务逻辑
             */
            resp = ControllerAspectHelper.checkParamValidateResult(params);

            /**
             * 业务逻辑调用部分
             */
            if (resp.hasSuccess()) {
                resp = (Resp) pjp.proceed();
            }
        } catch (Throwable ex) {
            resp = Resp.error(ex);
            errorLogger.error("controller error: ", ex);
        } finally {
            context.setResp(resp);
            if (!resp.hasSuccess()) {
                logTracer.traceError(null, context);
            }
            logTracer.traceEnd(context);
        }
        return resp;
    }

    private static class ControllerAspectHelper {
        static final List<String> PARAM_REQUIRED_CODE_LIST = Lists.newArrayList(
                NotBlank.class.getSimpleName(),
                NotEmpty.class.getSimpleName(),
                NotNull.class.getSimpleName()
        );

        /**
         * 参数验证结果检查
         *
         * @param params
         * @throws ServiceException
         */
        static Resp checkParamValidateResult(Object[] params) throws ServiceException {
            if (ArrayUtils.isNotEmpty(params)) {
                return Resp.success();
            }

            //从参数列表中提取BindingResult
            BindingResult bindingResult = null;
            for (Object param : params) {
                if (param instanceof BindingResult) {
                    bindingResult = (BindingResult) param;
                    break;
                }
            }

            if (bindingResult != null && bindingResult.hasErrors()) {
                List<ObjectError> objectErrorList = bindingResult.getAllErrors();
                for (ObjectError objectError : objectErrorList) {
                    String objectName = objectError.getObjectName();
                    String defaultMessage = objectError.getDefaultMessage();
                    String code = objectError.getCode();
                    if (PARAM_REQUIRED_CODE_LIST.contains(code)) {
                        return Resp.error(ErrorCode.PARAM_REQUIRED.getCode(), String.format(ErrorCode.PARAM_REQUIRED.getMessage(), objectName));
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(objectName).append(" : ").append(defaultMessage);
                        return Resp.error(ErrorCode.PARAM_INVALID.getCode(), String.format(ErrorCode.PARAM_INVALID.getMessage(), objectName));
                    }
                }
            }

            return Resp.success();
        }
    }

    private static class HttpReceiveTraceContext extends LogTracer.TraceContext {
        static Joiner joiner = Joiner.on(SymbolConstant.COMMA);
        private ProceedingJoinPoint pjp;
        private Resp resp;

        public HttpReceiveTraceContext(ProceedingJoinPoint pjp) {
            this.pjp = pjp;
        }

        public void setResp(Resp resp) {
            this.resp = resp;
        }

        @Override
        protected String fetchParam() {
            Object[] params = pjp.getArgs();
            if (ArrayUtils.isEmpty(params)) {
                return SymbolConstant.EMPTY;
            }

            StringBuilder sb = new StringBuilder();
            List<Object> paramList = Lists.newArrayListWithExpectedSize(params.length);
            for (Object param : params) {
                if (param instanceof BindingResult
                        || param instanceof ServletRequest) {
                    continue;
                }
                paramList.add(param);
            }

            joiner.appendTo(sb, paramList);
            return sb.toString();
        }

        //详细入参
        @Override
        protected String fetchDetailParam() {
            return fetchParam();
        }

        //详细结果
        @Override
        protected String fetchDetailResult() {
            return resp.toString();
        }

        //简单结果不返回data
        @Override
        protected String fetchResult() {
            return resp.toSimpleString();
        }

        @Override
        protected String fetchError(Throwable throwable) {
            return resp.toSimpleString();
        }

        @Override
        protected String fetchKind() {
            return "httpReceive";
        }

        @Override
        protected String fetchName() {
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return httpServletRequest.getRequestURI();
        }
    }
}

package com.supermarkettracker.infrastructure.web.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Registra uma trilha de auditoria sem persistir corpo, credenciais ou dados sensíveis. */
@Component
public class AuditoriaRequestInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaRequestInterceptor.class);
    private static final String START_NANOS_ATTRIBUTE = AuditoriaRequestInterceptor.class.getName() + ".startNanos";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_NANOS_ATTRIBUTE, System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        Long startedAt = (Long) request.getAttribute(START_NANOS_ATTRIBUTE);
        long elapsedMillis = startedAt == null ? 0 : (System.nanoTime() - startedAt) / 1_000_000;
        LOGGER.info("audit traceId={} method={} path={} status={} durationMs={}",
                request.getAttribute(RequestCorrelationFilter.TRACE_ID_ATTRIBUTE), request.getMethod(),
                request.getRequestURI(), response.getStatus(), elapsedMillis);
    }
}

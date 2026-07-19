package com.supermarkettracker.infrastructure.config;

import com.supermarkettracker.infrastructure.web.logging.AuditoriaRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAuditConfiguration implements WebMvcConfigurer {
    private final AuditoriaRequestInterceptor auditoria;
    public WebAuditConfiguration(AuditoriaRequestInterceptor auditoria) { this.auditoria = auditoria; }
    @Override public void addInterceptors(InterceptorRegistry registry) { registry.addInterceptor(auditoria).addPathPatterns("/**"); }
}

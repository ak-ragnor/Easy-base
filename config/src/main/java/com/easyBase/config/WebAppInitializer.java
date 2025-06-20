package com.easyBase.config;

import jakarta.servlet.*;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import java.util.EnumSet;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        // Single context approach - all configs in root
        return new Class<?>[] {
                RootConfig.class,
                SecurityConfig.class,
                DataConfig.class,
                WebMvcConfig.class  // MVC also in root to avoid issues
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // Empty - using single context approach
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/api/*" };
    }

    @Override
    protected Filter[] getServletFilters() {
        // Character encoding filter
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);

        // Note: Spring Security filter is auto-registered via @EnableWebSecurity
        // Other filters will be defined as Spring beans in configuration

        return new Filter[] {
                encodingFilter
        };
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // Set load-on-startup priority
        registration.setLoadOnStartup(1);

        // Set async support for modern REST APIs
        registration.setAsyncSupported(true);

        // Initialize parameters if needed
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        // Set session tracking mode to avoid URL rewriting
        servletContext.setSessionTrackingModes(
                EnumSet.of(SessionTrackingMode.COOKIE)
        );

        // Register additional filters if needed
        // Note: Most filters should be Spring beans for better management
    }
}
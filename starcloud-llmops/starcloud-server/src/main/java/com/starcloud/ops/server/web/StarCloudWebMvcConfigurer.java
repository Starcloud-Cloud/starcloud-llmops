package com.starcloud.ops.server.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-01
 */
@Configuration
public class StarCloudWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new CommonResultSseMessageConverter());
    }
}

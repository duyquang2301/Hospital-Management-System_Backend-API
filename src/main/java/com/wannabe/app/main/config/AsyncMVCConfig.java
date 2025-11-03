package com.wannabe.app.main.config;

import com.wannabe.app.main.interceptor.AuthInterceptor;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncMVCConfig implements WebMvcConfigurer {

    private final AuthInterceptor interceptor;

    public AsyncMVCConfig(@Lazy AuthInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Primary
    @Bean
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(1000);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("task-");
        executor.initialize();
        return executor;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(asyncTaskExecutor());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
            .addPathPatterns(
                "/auth/**",
                "/notification/**",
                "/terms/**",
                "/hospital/**",
                "/doctors/**",
                "/home/**",
                "/user/**",
                "/events/**",
                "/community/**",
                "/virtuality/**",
                "/comments/**",
                "/auth/**",
                "/plastic/**",
                "/chatting/**",
                "/push/**"
            )
            .excludePathPatterns(
                "/static/**",
                "/swagger*/**",
                "/webjars/**",
                "/v3/api-docs*/**",
                "/configuration*/**",
                "/_test/**",
                "/error"
            );
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
            .setConnectTimeout(Duration.ofMillis(50000)) // connection-timeout
            .setReadTimeout(Duration.ofMillis(150000)) // read-timeout
            .additionalMessageConverters(new StringHttpMessageConverter(StandardCharsets.UTF_8))
            .build();
    }
}

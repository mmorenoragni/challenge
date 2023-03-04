package com.example.challenge.config;


import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpStatusCodeException;

@Configuration
public class RetryTemplateConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
        policy.setExceptionClassifier(configureStatusCodeBasedRetryPolicy());
        retryTemplate.setRetryPolicy(policy);
        return retryTemplate;
    }

    private Classifier<Throwable, RetryPolicy> configureStatusCodeBasedRetryPolicy() {
        //one execution + 3 retries
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(4);
        NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

        return throwable -> {
            if (throwable instanceof HttpStatusCodeException) {
                return simpleRetryPolicy;
            }
            return neverRetryPolicy;
        };
    }
}

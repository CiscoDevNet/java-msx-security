package com.cisco.msx.integration.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.cloud.client.loadbalancer.RetryLoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * LoadBalancerConfiguration
 *
 * @author Livan Du
 * Created on 2018-10-19
 */
@Configuration
@ConditionalOnClass(LoadBalancerClient.class)
public class LoadBalancerConfig {

    @Bean
    public LoadBalancingRestTemplateCustomizer loadBalancingRestTemplateCustomizer(
            ObjectProvider<LoadBalancerInterceptor> loadBalancerInterceptor,
            ObjectProvider<RetryLoadBalancerInterceptor> retryLoadBalancerInterceptor) {

        return new LoadBalancingRestTemplateCustomizer(loadBalancerInterceptor, retryLoadBalancerInterceptor);
    }

    @AllArgsConstructor
    public static class LoadBalancingRestTemplateCustomizer implements RestTemplateCustomizer {

        private ObjectProvider<LoadBalancerInterceptor> loadBalancerInterceptor;

        private ObjectProvider<RetryLoadBalancerInterceptor> retryLoadBalancerInterceptor;

        @Override
        public void customize(RestTemplate restTemplate) {
            ClientHttpRequestInterceptor interceptor =
                    Optional.<ClientHttpRequestInterceptor>ofNullable(retryLoadBalancerInterceptor.getIfUnique())
                            .orElse(loadBalancerInterceptor.getIfUnique());

            if (interceptor != null) {
                List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
                interceptors.add(interceptor);
                restTemplate.setInterceptors(interceptors);
            }
        }
    }
}

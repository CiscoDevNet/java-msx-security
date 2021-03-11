/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.integration.config;

import com.cisco.msx.integration.MsxHttpClientFactory;
import com.cisco.msx.integration.MsxHttpClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
@PropertySource("classpath:/defaults-rest.properties")
@EnableConfigurationProperties({
        MsxHttpClientProperties.class
})
public class IntegrationConfig {

    @Bean
    public MsxHttpClientFactory msxHttpClientFactory(MsxHttpClientProperties msxHttpClientProperties) {
        return new MsxHttpClientFactory(msxHttpClientProperties);
    }

    @Bean
    public RestTemplateCustomizer msxRestTemplateCustomizer(MsxHttpClientFactory msxHttpClientFactory) {
        return restTemplate -> {
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(msxHttpClientFactory.createHttpClient()));
        };
    }
}

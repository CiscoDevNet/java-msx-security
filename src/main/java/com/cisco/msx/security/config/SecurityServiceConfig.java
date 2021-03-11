/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security.config;


import com.cisco.msx.security.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:/defaults-security.properties")
@PropertySource("classpath:/defaults-integration-security.properties")
@EnableConfigurationProperties({IntegrationSecurityProperties.class})
public class SecurityServiceConfig {

    @Bean
    public IdmSecurityApi idmSecurityApi(@Qualifier("basicAuthIdmRestTemplate") RestTemplate basicAuthIdmRestTemplate,
                                         IntegrationSecurityProperties integrationSecurityProperties) {
        return new IdmSecurityApi(basicAuthIdmRestTemplate, integrationSecurityProperties);
    }

    @Bean
    public SecurityContextBasedRBACUtils SecurityContextBasedRBACUtils(IdmSecurityApi idmSecurityApi, TokenBasedRBACUtils tokenBasedRBACUtils) {
        return new SecurityContextBasedRBACUtils(idmSecurityApi, tokenBasedRBACUtils);
    }

    @Bean
    public TokenBasedRBACUtils tokenBasedRBACUtils(IdmSecurityApi idmSecurityApi) {
        return new TokenBasedRBACUtils(idmSecurityApi);
    }

    @LoadBalanced
    @Bean("basicAuthIdmRestTemplate")
    public RestTemplate basicAuthIdmRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                                 IntegrationSecurityProperties integrationSecurityProperties) {
        return restTemplateBuilder
                .basicAuthentication(integrationSecurityProperties.getClientId(), integrationSecurityProperties.getClientSecret())
                .build();
    }
}

/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.autoconfigure;

import com.cisco.msx.integration.config.IntegrationConfig;
import com.cisco.msx.integration.config.LoadBalancerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfigureBefore({RestTemplateAutoConfiguration.class, LoadBalancerAutoConfiguration.class})
@Import({
        IntegrationConfig.class,
        LoadBalancerConfig.class
})
public class MsxIntegrationAutoConfiguration {
}

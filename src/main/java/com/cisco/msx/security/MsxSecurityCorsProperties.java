/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "security.cors")
@Validated
public class MsxSecurityCorsProperties {

    private boolean enabled = false;

    /**
     * Comma-separated list of origins to allow. '*' allows all origins. Default to '*'
     */
    private List<String> allowedOrigins = new ArrayList<String>(Arrays.asList("*"));

    /**
     * Comma-separated list of methods to allow. '*' allows all methods. Default to '*'
     */
    private List<String> allowedMethods = new ArrayList<String>(Arrays.asList("*"));

    /**
     * Comma-separated list of headers to allow in a request. '*' allows all headers. Default to '*'
     */
    private List<String> allowedHeaders = new ArrayList<String>(Arrays.asList("*"));

    /**
     * Comma-separated list of headers to include in a response.
     */
    private List<String> exposedHeaders = new ArrayList<>();

    /**
     * Whether credentials are supported. When not set, credentials are not supported.
     */
    private boolean allowCredentials = false;

    /**
     * How long the response from a pre-flight request can be cached by clients. If a
     * duration suffix is not specified, seconds will be used.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration maxAge = Duration.ofSeconds(1800);

    public CorsConfiguration toCorsConfiguration() {
        if (!this.enabled || CollectionUtils.isEmpty(this.allowedOrigins)) {
            return null;
        }
        PropertyMapper map = PropertyMapper.get();
        CorsConfiguration configuration = new CorsConfiguration();
        map.from(this::getAllowedOrigins).to(configuration::setAllowedOrigins);
        map.from(this::getAllowedHeaders).whenNot(CollectionUtils::isEmpty)
                .to(configuration::setAllowedHeaders);
        map.from(this::getAllowedMethods).whenNot(CollectionUtils::isEmpty)
                .to(configuration::setAllowedMethods);
        map.from(this::getExposedHeaders).whenNot(CollectionUtils::isEmpty)
                .to(configuration::setExposedHeaders);
        map.from(this::getMaxAge).whenNonNull().as(Duration::getSeconds)
                .to(configuration::setMaxAge);
        map.from(this::isAllowCredentials).whenNonNull()
                .to(configuration::setAllowCredentials);
        return configuration;
    }
}

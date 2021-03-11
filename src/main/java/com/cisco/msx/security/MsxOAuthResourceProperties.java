/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@ConfigurationProperties("security.resources")
@Validated
public class MsxOAuthResourceProperties {

    @NestedConfigurationProperty
    private List<ResourceSecurityRuleProperties> rules;

    @Data
    public static class ResourceSecurityRuleProperties {
        /**
         * Comma delimited patterns, required field
         */
        private List<String> patterns;

        /**
         * type of given patterns
         */
        private PatternType type = PatternType.ant;

        /**
         * Spring Expression Language (SpEL) of security rule.
         * e.g. hasRole('ROLE_CLIENT') and hasAuthority('SCOPE_read') and hasAuthority('SCOPE_write')
         */
        private String expr;


        public enum PatternType {
            ant, regex
        }
    }
}

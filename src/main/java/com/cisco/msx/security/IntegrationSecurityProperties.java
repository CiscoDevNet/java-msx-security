/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "integration.security")
@Validated
public class IntegrationSecurityProperties {

    public static final String DEFAULT_AUTH_SERVICE_NAME = "usermanagementservice-go";
    public static final String DEFAULT_AUTH_SERVICE_CONTEXT_PATH = "/idm";

    public static final String DEFAULT_CHECK_TOKEN_PATH = "/v2/check_token";
    public static final String DEFAULT_TENANT_HIERARCHY_ROOT_PATH = "/v2/tenant_hierarchy/root";
    public static final String DEFAULT_TENANT_HIERARCHY_PARENT_PATH = "/v2/tenant_hierarchy/parent";
    public static final String DEFAULT_TENANT_HIERARCHY_CHILDREN_PATH = "/v2/tenant_hierarchy/children";
    public static final String DEFAULT_TENANT_HIERARCHY_ANCESTORS_PATH = "/v2/tenant_hierarchy/ancestors";

    private String clientId;
    private String clientSecret;

    private AuthServerProperties server = new AuthServerProperties();

    private AuthEndpointProperties endpoints = new AuthEndpointProperties();

    @Data
    public static class AuthServerProperties {
        /**
         * service name that registered with service discovery
         */
        private String name = DEFAULT_AUTH_SERVICE_NAME;

        /**
         * service's context path
         */
        private String contextPath = DEFAULT_AUTH_SERVICE_CONTEXT_PATH;

        private String baseUrl = "http://" + DEFAULT_AUTH_SERVICE_NAME + DEFAULT_AUTH_SERVICE_CONTEXT_PATH;

    }

    @Data
    public static class AuthEndpointProperties {
        private String checkToken = DEFAULT_CHECK_TOKEN_PATH;
        private String tenantHierarchyRoot = DEFAULT_TENANT_HIERARCHY_ROOT_PATH;
        private String tenantHierarchyParent = DEFAULT_TENANT_HIERARCHY_PARENT_PATH;
        private String tenantHierarchyChildren = DEFAULT_TENANT_HIERARCHY_CHILDREN_PATH;
        private String tenantHierarchyAncestors = DEFAULT_TENANT_HIERARCHY_ANCESTORS_PATH;
    }

}

/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

/**
 * The client implementation of IDM security APIs
 */
@RequiredArgsConstructor
public class IdmSecurityApi {

    private final RestTemplate basicAuthIdmRestTemplate;

    private final IntegrationSecurityProperties integrationSecurityProperties;

    public SecurityContextDetails checkToken(String token) {
        SecurityContextDetails securityContextDetails = basicAuthIdmRestTemplate.postForObject(integrationSecurityProperties.getServer().getBaseUrl() +
                        integrationSecurityProperties.getEndpoints().getCheckToken() + "?token={token}",
                null, SecurityContextDetails.class, token);
        return securityContextDetails;
    }

    public String getTenantRoot() {
        return basicAuthIdmRestTemplate.getForObject(integrationSecurityProperties.getServer().getBaseUrl() +
                        integrationSecurityProperties.getEndpoints().getTenantHierarchyRoot(), String.class);
    }

    public String getTenantParent(String tenantId) {
        return basicAuthIdmRestTemplate.getForObject(integrationSecurityProperties.getServer().getBaseUrl() +
                integrationSecurityProperties.getEndpoints().getTenantHierarchyParent() + "?tenantId={tenantId}", String.class, tenantId);
    }

    public Set<String> getTenantChildren(String tenantId) {
        return basicAuthIdmRestTemplate.getForObject(integrationSecurityProperties.getServer().getBaseUrl() +
                integrationSecurityProperties.getEndpoints().getTenantHierarchyChildren() + "?tenantId={tenantId}", Set.class, tenantId);
    }

    public Set<String> getTenantAncestors(String tenantId) {
        return basicAuthIdmRestTemplate.getForObject(integrationSecurityProperties.getServer().getBaseUrl() +
                integrationSecurityProperties.getEndpoints().getTenantHierarchyAncestors() + "?tenantId={tenantId}", Set.class, tenantId);
    }
}

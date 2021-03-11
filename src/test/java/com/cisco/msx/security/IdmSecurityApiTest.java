/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class IdmSecurityApiTest {

    private IdmSecurityApi idmSecurityApi;

    @Mock
    private RestTemplate basicAuthIdmRestTemplate;

    @Mock
    private IntegrationSecurityProperties integrationSecurityProperties;

    @BeforeEach
    public void setUp() {
        idmSecurityApi = new IdmSecurityApi(basicAuthIdmRestTemplate, integrationSecurityProperties);
    }

    @Test
    public void checkToken() {
        // given
        String token = "token";
        String checkTokenEndpoint = "/v2/check_token";
        String baseUrl = "http://usermanagementservice/idm";

        IntegrationSecurityProperties.AuthEndpointProperties authEndpointProperties = mock(IntegrationSecurityProperties.AuthEndpointProperties.class);
        given(authEndpointProperties.getCheckToken()).willReturn(checkTokenEndpoint);
        given(integrationSecurityProperties.getEndpoints()).willReturn(authEndpointProperties);

        IntegrationSecurityProperties.AuthServerProperties authServerProperties = mock(IntegrationSecurityProperties.AuthServerProperties.class);
        given(authServerProperties.getBaseUrl()).willReturn(baseUrl);
        given(integrationSecurityProperties.getServer()).willReturn(authServerProperties);

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(basicAuthIdmRestTemplate.postForObject(eq(baseUrl + checkTokenEndpoint + "?token={token}"),
                eq(null), same(SecurityContextDetails.class), eq(token))).willReturn(securityContextDetails);

        // when
        SecurityContextDetails result = idmSecurityApi.checkToken(token);

        // then
        assertSame(securityContextDetails, result);
    }

    @Test
    public void getTenantRoot() {
        // given
        String tenantHierarchyRootEndpoint = "/v2/tenant_hierarchy/root";
        String baseUrl = "http://usermanagementservice/idm";

        IntegrationSecurityProperties.AuthEndpointProperties authEndpointProperties = mock(IntegrationSecurityProperties.AuthEndpointProperties.class);
        given(authEndpointProperties.getTenantHierarchyRoot()).willReturn(tenantHierarchyRootEndpoint);
        given(integrationSecurityProperties.getEndpoints()).willReturn(authEndpointProperties);

        IntegrationSecurityProperties.AuthServerProperties authServerProperties = mock(IntegrationSecurityProperties.AuthServerProperties.class);
        given(authServerProperties.getBaseUrl()).willReturn(baseUrl);
        given(integrationSecurityProperties.getServer()).willReturn(authServerProperties);

        String tenantRootId = "tenantRootId";
        given(basicAuthIdmRestTemplate.getForObject(eq(baseUrl + tenantHierarchyRootEndpoint),
                same(String.class))).willReturn(tenantRootId);

        // when
        String result = idmSecurityApi.getTenantRoot();

        // then
        assertEquals(tenantRootId, result);
    }

    @Test
    public void getTenantParent() {
        // given
        String tenantId = "tenantId";
        String tenantHierarchyParentEndpoint = "/v2/tenant_hierarchy/parent";
        String baseUrl = "http://usermanagementservice/idm";

        IntegrationSecurityProperties.AuthEndpointProperties authEndpointProperties = mock(IntegrationSecurityProperties.AuthEndpointProperties.class);
        given(authEndpointProperties.getTenantHierarchyParent()).willReturn(tenantHierarchyParentEndpoint);
        given(integrationSecurityProperties.getEndpoints()).willReturn(authEndpointProperties);

        IntegrationSecurityProperties.AuthServerProperties authServerProperties = mock(IntegrationSecurityProperties.AuthServerProperties.class);
        given(authServerProperties.getBaseUrl()).willReturn(baseUrl);
        given(integrationSecurityProperties.getServer()).willReturn(authServerProperties);

        String tenantParentId = "tenantParentId";
        given(basicAuthIdmRestTemplate.getForObject(eq(baseUrl + tenantHierarchyParentEndpoint + "?tenantId={tenantId}"),
                same(String.class), eq(tenantId))).willReturn(tenantParentId);

        // when
        String result = idmSecurityApi.getTenantParent(tenantId);

        // then
        assertEquals(tenantParentId, result);
    }

    @Test
    public void getTenantChildren() {
        // given
        String tenantId = "tenantId";
        String tenantHierarchyChildrenEndpoint = "/v2/tenant_hierarchy/children";
        String baseUrl = "http://usermanagementservice/idm";

        IntegrationSecurityProperties.AuthEndpointProperties authEndpointProperties = mock(IntegrationSecurityProperties.AuthEndpointProperties.class);
        given(authEndpointProperties.getTenantHierarchyChildren()).willReturn(tenantHierarchyChildrenEndpoint);
        given(integrationSecurityProperties.getEndpoints()).willReturn(authEndpointProperties);

        IntegrationSecurityProperties.AuthServerProperties authServerProperties = mock(IntegrationSecurityProperties.AuthServerProperties.class);
        given(authServerProperties.getBaseUrl()).willReturn(baseUrl);
        given(integrationSecurityProperties.getServer()).willReturn(authServerProperties);

        Set<String> tenantChildren = Set.of("tenantId1", "tenantId2");
        given(basicAuthIdmRestTemplate.getForObject(eq(baseUrl + tenantHierarchyChildrenEndpoint + "?tenantId={tenantId}"),
                same(Set.class), eq(tenantId))).willReturn(tenantChildren);

        // when
        Set<String> result = idmSecurityApi.getTenantChildren(tenantId);

        // then
        assertSame(tenantChildren, result);
    }

    @Test
    public void getTenantAncestors() {
        // given
        String tenantId = "tenantId";
        String tenantHierarchyAncestorsEndpoint = "/v2/tenant_hierarchy/ancestors";
        String baseUrl = "http://usermanagementservice/idm";

        IntegrationSecurityProperties.AuthEndpointProperties authEndpointProperties = mock(IntegrationSecurityProperties.AuthEndpointProperties.class);
        given(authEndpointProperties.getTenantHierarchyAncestors()).willReturn(tenantHierarchyAncestorsEndpoint);
        given(integrationSecurityProperties.getEndpoints()).willReturn(authEndpointProperties);

        IntegrationSecurityProperties.AuthServerProperties authServerProperties = mock(IntegrationSecurityProperties.AuthServerProperties.class);
        given(authServerProperties.getBaseUrl()).willReturn(baseUrl);
        given(integrationSecurityProperties.getServer()).willReturn(authServerProperties);

        Set<String> tenantAncestors = Set.of("tenantId1", "tenantId2");
        given(basicAuthIdmRestTemplate.getForObject(eq(baseUrl + tenantHierarchyAncestorsEndpoint + "?tenantId={tenantId}"),
                same(Set.class), eq(tenantId))).willReturn(tenantAncestors);

        // when
        Set<String> result = idmSecurityApi.getTenantAncestors(tenantId);

        // then
        assertSame(tenantAncestors, result);
    }
}

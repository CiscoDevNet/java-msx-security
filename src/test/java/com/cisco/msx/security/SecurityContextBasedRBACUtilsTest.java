/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                SecurityContextBasedRBACUtils.class
        }
)
public class SecurityContextBasedRBACUtilsTest {

    @SpyBean
    private SecurityContextBasedRBACUtils securityContextBasedRBACUtils;

    @MockBean
    private IdmSecurityApi idmSecurityApi;

    @MockBean
    private TokenBasedRBACUtils tokenBasedRBACUtils;

    @Test
    public void extractToken() {
        // given
        String token = "token";

        Jwt jwt = mock(Jwt.class);
        given(jwt.getTokenValue()).willReturn(token);

        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(jwtAuthenticationToken);
        SecurityContextHolder.setContext(securityContext);

        // when
        String result = securityContextBasedRBACUtils.extractToken();

        // then
        assertEquals(token, result);
        then(jwt).should().getTokenValue();
        then(securityContext).should().getAuthentication();
    }

    @Test
    public void extractToken_invalid_authentication() {
        // given
        SecurityContextHolder.clearContext();

        // when
        assertThrows(IllegalStateException.class, () -> securityContextBasedRBACUtils.extractToken());
    }

    @Test
    public void getSecurityContextDetails() {
        // given
        String token = "token";

        willReturn(token).given(securityContextBasedRBACUtils).extractToken();
        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(idmSecurityApi.checkToken(token)).willReturn(securityContextDetails);

        // when
        SecurityContextDetails result = securityContextBasedRBACUtils.getSecurityContextDetails();

        // then
        assertSame(securityContextDetails, result);
        then(securityContextBasedRBACUtils).should().extractToken();
        then(idmSecurityApi).should().checkToken(token);
    }

    @Test
    public void hasPermission() {
        // given
        String permission = "permission1";

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        willReturn(securityContextDetails).given(securityContextBasedRBACUtils).getSecurityContextDetails();
        given(tokenBasedRBACUtils.hasPermission(securityContextDetails, permission)).willReturn(true);

        // when
        boolean result = securityContextBasedRBACUtils.hasPermission(permission);

        // then
        assertTrue(result);
        then(securityContextBasedRBACUtils).should().getSecurityContextDetails();
        then(tokenBasedRBACUtils).should().hasPermission(securityContextDetails, permission);
    }

    @Test
    public void hasAccessAllTenantsPermission() {
        // given
        willReturn(true).given(securityContextBasedRBACUtils).hasPermission(Constants.ACCESS_ALL_TENANTS);

        // when
        boolean result = securityContextBasedRBACUtils.hasAccessAllTenantsPermission();

        // then
        assertTrue(result);
        then(securityContextBasedRBACUtils).should().hasPermission(Constants.ACCESS_ALL_TENANTS);
    }

    @Test
    public void hasAccessToTenant_tenantIdInvalid() {
        // given
        String tenantId = "tenantId1";
        given(tokenBasedRBACUtils.isTenantIdValid(tenantId)).willReturn(false);

        // when
        boolean result = securityContextBasedRBACUtils.hasAccessToTenant(tenantId);

        // then
        assertFalse(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
    }

    @Test
    public void hasAccessToTenant_hasAccessAllTenantsPermission() {
        // given
        String tenantId = "tenantId1";
        given(tokenBasedRBACUtils.isTenantIdValid(tenantId)).willReturn(true);
        willReturn(true).given(securityContextBasedRBACUtils).hasAccessAllTenantsPermission();

        // when
        boolean result = securityContextBasedRBACUtils.hasAccessToTenant(tenantId);

        // then
        assertTrue(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
        then(securityContextBasedRBACUtils).should().hasAccessAllTenantsPermission();
    }

    @Test
    public void hasAccessToTenant_tenantIdIsAssigned() {
        // given
        String tenantId = "tenantId1";
        List<String> assignedTenants = List.of(tenantId);
        given(tokenBasedRBACUtils.isTenantIdValid(tenantId)).willReturn(true);
        willReturn(false).given(securityContextBasedRBACUtils).hasAccessAllTenantsPermission();

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(securityContextDetails.getAssignedTenants()).willReturn(assignedTenants);
        willReturn(securityContextDetails).given(securityContextBasedRBACUtils).getSecurityContextDetails();

        // when
        boolean result = securityContextBasedRBACUtils.hasAccessToTenant(tenantId);

        // then
        assertTrue(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
        then(securityContextBasedRBACUtils).should().hasAccessAllTenantsPermission();
        then(securityContextDetails).should().getAssignedTenants();
        then(securityContextBasedRBACUtils).should().getSecurityContextDetails();
    }

    @Test
    public void hasAccessToTenant_OneOfAncestorTenantIsAssigned() {
        // given
        String tenantId = "tenantId1";
        List<String> assignedTenants = List.of("ancestorTenantId1");
        Set<String> ancestorTenants = Set.of("ancestorTenantId1", "ancestorTenantId2");
        given(tokenBasedRBACUtils.isTenantIdValid(tenantId)).willReturn(true);
        willReturn(false).given(securityContextBasedRBACUtils).hasAccessAllTenantsPermission();

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(securityContextDetails.getAssignedTenants()).willReturn(assignedTenants);
        willReturn(securityContextDetails).given(securityContextBasedRBACUtils).getSecurityContextDetails();
        given(idmSecurityApi.getTenantAncestors(tenantId)).willReturn(ancestorTenants);

        // when
        boolean result = securityContextBasedRBACUtils.hasAccessToTenant(tenantId);

        // then
        assertTrue(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
        then(securityContextBasedRBACUtils).should().hasAccessAllTenantsPermission();
        then(securityContextDetails).should().getAssignedTenants();
        then(idmSecurityApi).should().getTenantAncestors(tenantId);
    }

    @Test
    public void hasAccessToTenant_noAncestorTenantIsAssigned() {
        // given
        String tenantId = "tenantId1";
        List<String> assignedTenants = List.of("ancestorTenantId1");
        Set<String> ancestorTenants = Set.of("ancestorTenantId2", "ancestorTenantId3");
        given(tokenBasedRBACUtils.isTenantIdValid(tenantId)).willReturn(true);
        willReturn(false).given(securityContextBasedRBACUtils).hasAccessAllTenantsPermission();

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(securityContextDetails.getAssignedTenants()).willReturn(assignedTenants);
        willReturn(securityContextDetails).given(securityContextBasedRBACUtils).getSecurityContextDetails();
        given(idmSecurityApi.getTenantAncestors(tenantId)).willReturn(ancestorTenants);

        // when
        boolean result = securityContextBasedRBACUtils.hasAccessToTenant(tenantId);

        // then
        assertFalse(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
        then(securityContextBasedRBACUtils).should().hasAccessAllTenantsPermission();
        then(securityContextDetails).should().getAssignedTenants();
        then(securityContextBasedRBACUtils).should().getSecurityContextDetails();
        then(idmSecurityApi).should().getTenantAncestors(tenantId);
    }

    @Test
    public void hasAccessToTenants_haveAccessToAllTenants() {
        // given
        String tenantId1 = "tenantId1";
        String tenantId2 = "tenantId2";
        String tenantId3 = "tenantId3";
        List<String> tenantIds = List.of(tenantId1, tenantId2, tenantId3);
        willReturn(true).given(securityContextBasedRBACUtils).hasAccessToTenant(tenantId1);
        willReturn(true).given(securityContextBasedRBACUtils).hasAccessToTenant(tenantId2);
        willReturn(true).given(securityContextBasedRBACUtils).hasAccessToTenant(tenantId3);

        // when
        boolean result = securityContextBasedRBACUtils.hasAccessToTenants(tenantIds);

        // then
        assertTrue(result);
        then(securityContextBasedRBACUtils).should().hasAccessToTenant(tenantId1);
        then(securityContextBasedRBACUtils).should().hasAccessToTenant(tenantId2);
        then(securityContextBasedRBACUtils).should().hasAccessToTenant(tenantId3);
    }

    @Test
    public void hasAccessToTenants_haveNotAccessToAllTenants() {
        // given
        String tenantId1 = "tenantId1";
        String tenantId2 = "tenantId2";
        String tenantId3 = "tenantId3";
        List<String> tenantIds = List.of(tenantId1, tenantId2, tenantId3);
        willReturn(true).given(securityContextBasedRBACUtils).hasAccessToTenant(tenantId1);
        willReturn(false).given(securityContextBasedRBACUtils).hasAccessToTenant(tenantId2);
        willReturn(true).given(securityContextBasedRBACUtils).hasAccessToTenant(tenantId3);

        // when
        boolean result = securityContextBasedRBACUtils.hasAccessToTenants(tenantIds);

        // then
        assertFalse(result);
        then(securityContextBasedRBACUtils).should().hasAccessToTenant(tenantId1);
        then(securityContextBasedRBACUtils).should().hasAccessToTenant(tenantId2);
        then(securityContextBasedRBACUtils).should(times(0)).hasAccessToTenant(tenantId3);
    }
}

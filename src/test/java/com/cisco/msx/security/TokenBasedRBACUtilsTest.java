/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                TokenBasedRBACUtils.class
        }
)
public class TokenBasedRBACUtilsTest {

    @SpyBean
    private TokenBasedRBACUtils tokenBasedRBACUtils;

    @MockBean
    private IdmSecurityApi idmSecurityApi;


    @Test
    public void hasPermission_withToken() {
        // given
        String oauth2Token = "oauth2Token1";
        String permission = "permission1";

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(idmSecurityApi.checkToken(oauth2Token)).willReturn(securityContextDetails);
        willReturn(true).given(tokenBasedRBACUtils).hasPermission(securityContextDetails, permission);

        // when
        boolean result = tokenBasedRBACUtils.hasPermission(oauth2Token, permission);

        // then
        assertTrue(result);
        then(idmSecurityApi).should().checkToken(oauth2Token);
        then(tokenBasedRBACUtils).should().hasPermission(securityContextDetails, permission);
    }

    @Test
    public void hasPermission_withSecurityContextDetails() {
        // given
        String permission = "permission1";
        List<String> permissions = List.of(permission);

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(securityContextDetails.getPermissions()).willReturn(permissions);

        // when
        boolean result = tokenBasedRBACUtils.hasPermission(securityContextDetails, permission);

        // then
        assertTrue(result);
        then(securityContextDetails).should().getPermissions();
    }

    @Test
    public void hasAccessAllTenantsPermission() {
        // given
        String oauth2Token = "oauth2Token1";

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(idmSecurityApi.checkToken(oauth2Token)).willReturn(securityContextDetails);
        willReturn(true).given(tokenBasedRBACUtils).hasPermission(securityContextDetails, Constants.ACCESS_ALL_TENANTS);

        // when
        boolean result = tokenBasedRBACUtils.hasAccessAllTenantsPermission(oauth2Token);

        // then
        assertTrue(result);
        then(idmSecurityApi).should().checkToken(oauth2Token);
        then(tokenBasedRBACUtils).should().hasPermission(securityContextDetails, Constants.ACCESS_ALL_TENANTS);
    }

    @Test
    public void hasAccessToTenant_tenantIdInvalid() {
        // given
        String oauth2Token = "oauth2Token1";
        String tenantId = "tenantId1";
        willReturn(false).given(tokenBasedRBACUtils).isTenantIdValid(tenantId);

        // when
        boolean result = tokenBasedRBACUtils.hasAccessToTenant(oauth2Token, tenantId);

        // then
        assertFalse(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
    }

    @Test
    public void hasAccessToTenant_hasAccessAllTenantsPermission() {
        // given
        String oauth2Token = "oauth2Token1";
        String tenantId = "tenantId1";
        willReturn(true).given(tokenBasedRBACUtils).isTenantIdValid(tenantId);
        willReturn(true).given(tokenBasedRBACUtils).hasAccessAllTenantsPermission(oauth2Token);

        // when
        boolean result = tokenBasedRBACUtils.hasAccessToTenant(oauth2Token, tenantId);

        // then
        assertTrue(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
        then(tokenBasedRBACUtils).should().hasAccessAllTenantsPermission(oauth2Token);
    }

    @Test
    public void hasAccessToTenant_tenantIdIsAssigned() {
        // given
        String oauth2Token = "oauth2Token1";
        String tenantId = "tenantId1";
        List<String> assignedTenants = List.of(tenantId);
        willReturn(true).given(tokenBasedRBACUtils).isTenantIdValid(tenantId);
        willReturn(false).given(tokenBasedRBACUtils).hasAccessAllTenantsPermission(oauth2Token);

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(securityContextDetails.getAssignedTenants()).willReturn(assignedTenants);
        given(idmSecurityApi.checkToken(oauth2Token)).willReturn(securityContextDetails);

        // when
        boolean result = tokenBasedRBACUtils.hasAccessToTenant(oauth2Token, tenantId);

        // then
        assertTrue(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
        then(tokenBasedRBACUtils).should().hasAccessAllTenantsPermission(oauth2Token);
        then(securityContextDetails).should().getAssignedTenants();
        then(idmSecurityApi).should().checkToken(oauth2Token);
    }

    @Test
    public void hasAccessToTenant_OneOfAncestorTenantIsAssigned() {
        // given
        String oauth2Token = "oauth2Token1";
        String tenantId = "tenantId1";
        List<String> assignedTenants = List.of("ancestorTenantId1");
        Set<String> ancestorTenants = Set.of("ancestorTenantId1", "ancestorTenantId2");
        willReturn(true).given(tokenBasedRBACUtils).isTenantIdValid(tenantId);
        willReturn(false).given(tokenBasedRBACUtils).hasAccessAllTenantsPermission(oauth2Token);

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(securityContextDetails.getAssignedTenants()).willReturn(assignedTenants);
        given(idmSecurityApi.checkToken(oauth2Token)).willReturn(securityContextDetails);

        given(idmSecurityApi.getTenantAncestors(tenantId)).willReturn(ancestorTenants);

        // when
        boolean result = tokenBasedRBACUtils.hasAccessToTenant(oauth2Token, tenantId);

        // then
        assertTrue(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
        then(tokenBasedRBACUtils).should().hasAccessAllTenantsPermission(oauth2Token);
        then(securityContextDetails).should().getAssignedTenants();
        then(idmSecurityApi).should().checkToken(oauth2Token);
        then(idmSecurityApi).should().getTenantAncestors(tenantId);
    }

    @Test
    public void hasAccessToTenant_noAncestorTenantIsAssigned() {
        // given
        String oauth2Token = "oauth2Token1";
        String tenantId = "tenantId1";
        List<String> assignedTenants = List.of("ancestorTenantId1");
        Set<String> ancestorTenants = Set.of("ancestorTenantId2", "ancestorTenantId3");
        willReturn(true).given(tokenBasedRBACUtils).isTenantIdValid(tenantId);
        willReturn(false).given(tokenBasedRBACUtils).hasAccessAllTenantsPermission(oauth2Token);

        SecurityContextDetails securityContextDetails = mock(SecurityContextDetails.class);
        given(securityContextDetails.getAssignedTenants()).willReturn(assignedTenants);
        given(idmSecurityApi.checkToken(oauth2Token)).willReturn(securityContextDetails);

        given(idmSecurityApi.getTenantAncestors(tenantId)).willReturn(ancestorTenants);

        // when
        boolean result = tokenBasedRBACUtils.hasAccessToTenant(oauth2Token, tenantId);

        // then
        assertFalse(result);
        then(tokenBasedRBACUtils).should().isTenantIdValid(tenantId);
        then(tokenBasedRBACUtils).should().hasAccessAllTenantsPermission(oauth2Token);
        then(securityContextDetails).should().getAssignedTenants();
        then(idmSecurityApi).should().checkToken(oauth2Token);
        then(idmSecurityApi).should().getTenantAncestors(tenantId);
    }

    @Test
    public void hasAccessToTenants_haveAccessToAllTenants() {
        // given
        String oauth2Token = "oauth2Token1";
        String tenantId1 = "tenantId1";
        String tenantId2 = "tenantId2";
        String tenantId3 = "tenantId3";
        List<String> tenantIds = List.of(tenantId1, tenantId2, tenantId3);
        willReturn(true).given(tokenBasedRBACUtils).hasAccessToTenant(oauth2Token, tenantId1);
        willReturn(true).given(tokenBasedRBACUtils).hasAccessToTenant(oauth2Token, tenantId2);
        willReturn(true).given(tokenBasedRBACUtils).hasAccessToTenant(oauth2Token, tenantId3);

        // when
        boolean result = tokenBasedRBACUtils.hasAccessToTenants(oauth2Token, tenantIds);

        // then
        assertTrue(result);
        then(tokenBasedRBACUtils).should().hasAccessToTenant(oauth2Token, tenantId1);
        then(tokenBasedRBACUtils).should().hasAccessToTenant(oauth2Token, tenantId2);
        then(tokenBasedRBACUtils).should().hasAccessToTenant(oauth2Token, tenantId3);
    }

    @Test
    public void hasAccessToTenants_haveNotAccessToAllTenants() {
        // given
        String oauth2Token = "oauth2Token1";
        String tenantId1 = "tenantId1";
        String tenantId2 = "tenantId2";
        String tenantId3 = "tenantId3";
        List<String> tenantIds = List.of(tenantId1, tenantId2, tenantId3);
        willReturn(true).given(tokenBasedRBACUtils).hasAccessToTenant(oauth2Token, tenantId1);
        willReturn(false).given(tokenBasedRBACUtils).hasAccessToTenant(oauth2Token, tenantId2);
        willReturn(true).given(tokenBasedRBACUtils).hasAccessToTenant(oauth2Token, tenantId3);

        // when
        boolean result = tokenBasedRBACUtils.hasAccessToTenants(oauth2Token, tenantIds);

        // then
        assertFalse(result);
        then(tokenBasedRBACUtils).should().hasAccessToTenant(oauth2Token, tenantId1);
        then(tokenBasedRBACUtils).should().hasAccessToTenant(oauth2Token, tenantId2);
        then(tokenBasedRBACUtils).should(times(0)).hasAccessToTenant(oauth2Token, tenantId3);
    }

    @Test
    public void isTenantIdValid_hasParent() {
        // given
        String tenantId = "tenantId1";
        String parentTenantId = "tenantId2";
        given(idmSecurityApi.getTenantParent(tenantId)).willReturn(parentTenantId);

        // when
        boolean result = tokenBasedRBACUtils.isTenantIdValid(tenantId);

        // then
        assertTrue(result);
        then(idmSecurityApi).should().getTenantParent(tenantId);
    }

    @Test
    public void isTenantIdValid_isRootTenant() {
        // given
        String tenantId = "tenantId";
        given(idmSecurityApi.getTenantParent(tenantId)).willReturn(null);
        given(idmSecurityApi.getTenantRoot()).willReturn(tenantId);

        // when
        boolean result = tokenBasedRBACUtils.isTenantIdValid(tenantId);

        // then
        assertTrue(result);
        then(idmSecurityApi).should().getTenantParent(tenantId);
        then(idmSecurityApi).should().getTenantRoot();
    }

    @Test
    public void isTenantIdValid_invalidTenantId() {
        // given
        String tenantId = "tenantId";
        String rootTenantId = "rootTenantId";
        given(idmSecurityApi.getTenantParent(tenantId)).willReturn(null);
        given(idmSecurityApi.getTenantRoot()).willReturn(rootTenantId);

        // when
        boolean result = tokenBasedRBACUtils.isTenantIdValid(tenantId);

        // then
        assertFalse(result);
        then(idmSecurityApi).should().getTenantParent(tenantId);
        then(idmSecurityApi).should().getTenantRoot();
    }
}

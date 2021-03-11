/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * RBAC utils based on the SecurityContext. The permission and access check is based on the SecurityContextDetails
 * in SecurityContext and IDM tenant APIs. It can only be executed in the http servlet request handling thread or
 * a thread which inherits the thread local of the http servlet request handling thread.
 */
@RequiredArgsConstructor
public class SecurityContextBasedRBACUtils {

    private final IdmSecurityApi idmSecurityApi;
    private final TokenBasedRBACUtils tokenBasedRBACUtils;

    public SecurityContextDetails getSecurityContextDetails() {
        return idmSecurityApi.checkToken(extractToken());
    }

    public String extractToken() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new IllegalStateException("JwtAuthenticationToken not found in SecurityContext");
        }
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        return jwt.getTokenValue();
    }

    /**
     *  Check if the user associated with the current SecurityContextDetails has the specified permission
     * @param permission permission to check
     * @return true if the user has the permission; false otherwise
     */
    public boolean hasPermission(String permission) {
        SecurityContextDetails securityContextDetails = getSecurityContextDetails();
        return tokenBasedRBACUtils.hasPermission(securityContextDetails, permission);
    }

    /**
     * Check if the user associated with the current SecurityContextDetails has the permission Constants.ACCESS_ALL_TENANTS
     * @return true if the user has the permission; false otherwise
     */
    public boolean hasAccessAllTenantsPermission() {
        return hasPermission(Constants.ACCESS_ALL_TENANTS);
    }

    /**
     * Convenient method to check if the user associated with the current SecurityContextDetails has access to this tenant.
     * If the tenantId is not valid (i.e. does not exist), the return value will also be false.
     * @param tenantId tenantId to check
     * @return true if the user has access to the tenant; false otherwise
     */
    public boolean hasAccessToTenant(String tenantId) {
        if (!tokenBasedRBACUtils.isTenantIdValid(tenantId)){
            return false;
        }

        if (hasAccessAllTenantsPermission()){
            return true;
        }
        SecurityContextDetails securityContextDetails = getSecurityContextDetails();
        List<String> assignedTenants = securityContextDetails.getAssignedTenants();
        if (assignedTenants.contains(tenantId)) {
            return true;
        }
        Set<String> ancestors = idmSecurityApi.getTenantAncestors(tenantId);
        return ancestors.stream().anyMatch(ancestor -> assignedTenants.contains(ancestor));
    }

    /**
     * Convenient method to check if the user associated with the current SecurityContextDetails has access to every item in the list.
     * If any tenantId is not valid (i.e. does not exist), the return value will also be false.
     * @param tenantIds tenantIds to check
     * @return false if the user doesn't have access to any of the tenant id in the collection.
     */
    public boolean hasAccessToTenants(Collection<String> tenantIds) {
        for (String tenantId : tenantIds) {
            if (!hasAccessToTenant(tenantId)) {
                return false;
            }
        }
        return true;
    }

}

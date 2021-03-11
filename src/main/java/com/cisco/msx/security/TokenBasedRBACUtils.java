/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * RBAC utils based on the oauth2 token. It uses the oauth2 token to retrieve the SecurityContextDetails from IDM. The permission and access check
 *  is based on the SecurityContextDetails and the IDM tenant APIs
 */
@RequiredArgsConstructor
public class TokenBasedRBACUtils {

    private final IdmSecurityApi idmSecurityApi;

    /**
     *  Check if the user associated with the oauth2Token has the specified permission
     * @param oauth2Token oauth2 token
     * @param permission permission to check
     * @return true if the user has the permission; false otherwise
     */
    public boolean hasPermission(String oauth2Token, String permission) {
        SecurityContextDetails securityContextDetails = idmSecurityApi.checkToken(oauth2Token);
        return hasPermission(securityContextDetails, permission);
    }

    /**
     * Check if the user associated with the securityContextDetails has the specified permission
     * @param securityContextDetails securityContextDetails
     * @param permission permission to check
     * @return true if the user has the permission; false otherwise
     */
    public boolean hasPermission(SecurityContextDetails securityContextDetails, String permission) {
        List<String> permissions = securityContextDetails.getPermissions();
        return permissions.contains(permission);
    }

    /**
     * Check if the user associated with the oauth2Token has the permission Constants.ACCESS_ALL_TENANTS
     * @param oauth2Token
     * @return true if the user has the permission; false otherwise
     */
    public boolean hasAccessAllTenantsPermission(String oauth2Token) {
        SecurityContextDetails securityContextDetails = idmSecurityApi.checkToken(oauth2Token);
        return hasPermission(securityContextDetails, Constants.ACCESS_ALL_TENANTS);
    }

    /**
     * Convenient method to check if the user has access to this tenant.
     * If the tenantId is not valid (i.e. does not exist), the return value will also be false.
     * @param oauth2Token oauth2 token
     * @param tenantId tenantId to check
     * @return true if the user has access to the tenant; false otherwise
     */
    public boolean hasAccessToTenant(String oauth2Token, String tenantId) {
        if (!isTenantIdValid(tenantId)){
            return false;
        }

        if (hasAccessAllTenantsPermission(oauth2Token)){
            return true;
        }
        SecurityContextDetails securityContextDetails = idmSecurityApi.checkToken(oauth2Token);
        List<String> assignedTenants = securityContextDetails.getAssignedTenants();
        if (assignedTenants.contains(tenantId)) {
            return true;
        }
        Set<String> ancestors = idmSecurityApi.getTenantAncestors(tenantId);
        return ancestors.stream().anyMatch(ancestor -> assignedTenants.contains(ancestor));
    }

    /**
     * Convenient method to check if the user has access to every item in the list.
     * If any tenantId is not valid (i.e. does not exist), the return value will also be false.
     * @param oauth2Token oauth2 token
     * @param tenantIds tenantIds to check
     * @return false if the user doesn't have access to any of the tenant id in the collection.
     */
    public boolean hasAccessToTenants(String oauth2Token, Collection<String> tenantIds) {
        for (String tenantId : tenantIds) {
            if (!hasAccessToTenant(oauth2Token, tenantId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the tenantId is valid. If the tenant is valid, it must be in the tenant hierarchy either as a node (which has a parent), or as the root.
     * @param tenantId tenantId to check
     * @return true if the tenantId is valid; false otherwise
     */
    public boolean isTenantIdValid(String tenantId) {
        String parentId = idmSecurityApi.getTenantParent(tenantId);

        if (parentId != null && !parentId.isBlank()) {
            return true;
        } else {
            return idmSecurityApi.getTenantRoot().equals(tenantId);
        }
    }
}

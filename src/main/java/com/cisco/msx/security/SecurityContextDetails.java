/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Store the security context details returned from the IDM check_token API
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityContextDetails {

    private String iss;

    private String sub;

    private String aud;

    private int exp;

    private int iat;

    private String jti;

    @JsonProperty("auth_time")
    private int authTime;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String email;

    private String locale;

    private boolean active;

    @JsonProperty("scope")
    private List<String> scope;

    @JsonProperty("client_id")
    private String clientId;

    private String username;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("account_type")
    private String accountType;

    private String currency;

    @JsonProperty("tenant_id")
    private String tenantId;

    @JsonProperty("tenant_name")
    private String tenantName;

    @JsonProperty("provider_id")
    private String providerId;

    @JsonProperty("provider_name")
    private String providerName;

    @JsonProperty("provider_email")
    private String providerEmail;

    @JsonProperty("assigned_tenants")
    private List<String> assignedTenants;

    private List<String> roles;

    private List<String> permissions;

}

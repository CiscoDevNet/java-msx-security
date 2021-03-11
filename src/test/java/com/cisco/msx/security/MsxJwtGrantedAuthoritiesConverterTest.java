/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MsxJwtGrantedAuthoritiesConverterTest {

    private MsxJwtGrantedAuthoritiesConverter msxJwtGrantedAuthoritiesConverter;

    @BeforeEach
    public void setUp() {
        msxJwtGrantedAuthoritiesConverter = new MsxJwtGrantedAuthoritiesConverter();
    }

    @Test
    public void convert() {
        // given
        String role = "ROLE_CLIENT";
        String readScope = "read";
        String writeScope = "write";
        String scopePrefix = "SCOPE_";
        String token = "token";
        Jwt jwt = Jwt.withTokenValue(token)
                .claim("authorities", List.of(role))
                .claim("scope", List.of(readScope, writeScope))
                .header("typ", "JWT")
                .build();

        // when
        Collection<GrantedAuthority> grantedAuthorities = msxJwtGrantedAuthoritiesConverter.convert(jwt);

        // then
        assertNotNull(grantedAuthorities);
        assertTrue(grantedAuthorities.contains(new SimpleGrantedAuthority(role)));
        assertTrue(grantedAuthorities.contains(new SimpleGrantedAuthority(scopePrefix + readScope)));
        assertTrue(grantedAuthorities.contains(new SimpleGrantedAuthority(scopePrefix + writeScope)));
    }
}

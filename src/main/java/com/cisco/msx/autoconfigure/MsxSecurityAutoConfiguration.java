/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.autoconfigure;

import com.cisco.msx.security.config.OAuth2ResourceServerSecurityConfig;
import com.cisco.msx.security.config.SecurityServiceConfig;
import com.cisco.msx.security.config.SystemPropertyConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass({OAuth2AccessToken.class, WebMvcConfigurer.class})
@AutoConfigureBefore({WebMvcAutoConfiguration.class, SessionAutoConfiguration.class})
@Import({
        OAuth2ResourceServerSecurityConfig.class,
        SecurityServiceConfig.class,
        SystemPropertyConfig.class
})
public class MsxSecurityAutoConfiguration {

}

/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.security.config;

import com.cisco.msx.security.MsxJwtGrantedAuthoritiesConverter;
import com.cisco.msx.security.MsxOAuthResourceProperties;
import com.cisco.msx.security.MsxSecurityCorsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@EnableConfigurationProperties({MsxSecurityCorsProperties.class, MsxOAuthResourceProperties.class})
public class OAuth2ResourceServerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MsxOAuthResourceProperties msxOAuthResourceProperties;

    @Autowired
    private MsxSecurityCorsProperties msxSecurityCorsProperties;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        for (MsxOAuthResourceProperties.ResourceSecurityRuleProperties rule : msxOAuthResourceProperties.getRules()) {
            if (rule.getPatterns() == null || rule.getPatterns().isEmpty()) {
                continue;
            }

            // @formatter:off
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl;
            switch (rule.getType()) {
                case regex:
                    authorizedUrl = http.requestMatchers()
                            .regexMatchers(rule.getPatterns().toArray(new String[0]))
                            .and()
                            .authorizeRequests()
                            .regexMatchers(rule.getPatterns().toArray(new String[0]));
                    break;
                case ant:
                default:
                    authorizedUrl = http.requestMatchers()
                            .antMatchers(rule.getPatterns().toArray(new String[0]))
                            .and()
                            .authorizeRequests()
                            .antMatchers(rule.getPatterns().toArray(new String[0]));
                    break;
            }

            if (StringUtils.hasText(rule.getExpr())) {
                authorizedUrl.access(rule.getExpr());
            } else {
                authorizedUrl.fullyAuthenticated();
            }
            // @formatter:on
        }

        // @formatter:off
        http
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
                .and()
                .headers()
                .frameOptions().sameOrigin()
                .httpStrictTransportSecurity();
        // @formatter:on

        if (msxSecurityCorsProperties.isEnabled()) {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration configuration = msxSecurityCorsProperties.toCorsConfiguration();
            source.registerCorsConfiguration("/**", configuration);

            http.cors().configurationSource(source);
        }
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        MsxJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new MsxJwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Configuration
    public static class CorsWebMvcConfiguration implements WebMvcConfigurer {

        @Autowired
        private MsxSecurityCorsProperties msxSecurityCorsProperties;

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            if (msxSecurityCorsProperties.isEnabled()) {
                registry.addMapping("/**")
                        .allowedOrigins(msxSecurityCorsProperties.getAllowedOrigins().toArray(new String[0]))
                        .allowedHeaders(msxSecurityCorsProperties.getAllowedHeaders().toArray(new String[0]))
                        .allowedMethods(msxSecurityCorsProperties.getAllowedMethods().toArray(new String[0]))
                        .exposedHeaders(msxSecurityCorsProperties.getExposedHeaders().toArray(new String[0]))
                        .allowCredentials(msxSecurityCorsProperties.isAllowCredentials())
                        .maxAge(msxSecurityCorsProperties.getMaxAge().getSeconds());
            }
        }
    }

}


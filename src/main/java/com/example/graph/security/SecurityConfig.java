package com.example.graph.security;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(AuthSecurityProperties.class)
public class SecurityConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    @ConditionalOnProperty(prefix = "spring.security.oauth2.resourceserver.jwt", name = "issuer-uri")
    public SecurityFilterChain issuerJwtSecurityFilterChain(HttpSecurity http,
                                                            AuthSecurityProperties authSecurityProperties,
                                                            JwtAuthConverter jwtAuthConverter) throws Exception {
        return buildJwtChain(http, authSecurityProperties, jwtAuthConverter);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.security.oauth2.resourceserver.jwt", name = "jwk-set-uri")
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain jwkSetJwtSecurityFilterChain(HttpSecurity http,
                                                            AuthSecurityProperties authSecurityProperties,
                                                            JwtAuthConverter jwtAuthConverter) throws Exception {
        return buildJwtChain(http, authSecurityProperties, jwtAuthConverter);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain openSecurityFilterChain(HttpSecurity http) throws Exception {
        LOG.warn("JWT is disabled because issuer-uri/jwk-set-uri is not configured. ALL endpoints are temporarily open.");
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .oauth2ResourceServer(oauth -> oauth.disable());
        return http.build();
    }

    @Bean
    public JwtAuthConverter jwtAuthConverter() {
        return new JwtAuthConverter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.security.oauth2.resourceserver.jwt", name = "issuer-uri")
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder issuerJwtDecoder(AuthSecurityProperties authSecurityProperties,
                                       OAuth2ResourceServerProperties resourceServerProperties) {
        String issuerUri = resourceServerProperties.getJwt().getIssuerUri();
        NimbusJwtDecoder decoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
        configureDecoderValidators(decoder, authSecurityProperties, issuerUri);
        return decoder;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.security.oauth2.resourceserver.jwt", name = "jwk-set-uri")
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwkSetJwtDecoder(AuthSecurityProperties authSecurityProperties,
                                       OAuth2ResourceServerProperties resourceServerProperties) {
        String jwkSetUri = resourceServerProperties.getJwt().getJwkSetUri();
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        configureDecoderValidators(decoder, authSecurityProperties, null);
        return decoder;
    }

    private SecurityFilterChain buildJwtChain(HttpSecurity http,
                                              AuthSecurityProperties authSecurityProperties,
                                              JwtAuthConverter jwtAuthConverter) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/api/**"),
                new AntPathRequestMatcher("/public/**")))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/admin/**").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll();
                if (authSecurityProperties.isPermitPublic()) {
                    auth.requestMatchers(HttpMethod.GET, "/public/graph").permitAll();
                }
                auth.requestMatchers(HttpMethod.POST, "/public/**").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/public/**").authenticated()
                    .requestMatchers("/public/**", "/api/**").authenticated()
                    .anyRequest().authenticated();
            })
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));
        return http.build();
    }

    private void configureDecoderValidators(NimbusJwtDecoder decoder,
                                            AuthSecurityProperties authSecurityProperties,
                                            String issuerUri) {
        String expectedIssuer = StringUtils.hasText(authSecurityProperties.getExpectedIssuer())
            ? authSecurityProperties.getExpectedIssuer()
            : issuerUri;
        if (StringUtils.hasText(expectedIssuer)) {
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(expectedIssuer),
                new AudienceValidator(normalizeAudience(authSecurityProperties.getExpectedAudience()))));
        } else {
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                new AudienceValidator(normalizeAudience(authSecurityProperties.getExpectedAudience()))));
        }
    }

    private List<String> normalizeAudience(List<String> audiences) {
        if (audiences == null) {
            return List.of();
        }
        return audiences.stream().filter(StringUtils::hasText).toList();
    }
}

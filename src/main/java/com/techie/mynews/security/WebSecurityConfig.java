package com.techie.mynews.security;

import com.techie.mynews.security.jwt.AuthEntryPointJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final AuthEntryPointJwt unauthorizedHandler;

    Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

    JwtIssuerAuthenticationManagerResolver authenticationManagerResolver =
            new JwtIssuerAuthenticationManagerResolver(authenticationManagers::get);

    @Value("${spring.security.oauth2.resourceserver.jwt.microsoft.issuer-uri}")
    private String microsoftIssuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.google.issuer-uri}")
    private String googleIssuerUri;
    public WebSecurityConfig(AuthEntryPointJwt unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        addManager(authenticationManagers, microsoftIssuerUri);
        addManager(authenticationManagers, googleIssuerUri);

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/security/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(resourceServerConfig ->
                        resourceServerConfig.authenticationManagerResolver(authenticationManagerResolver));
        return http.build();
    }

    public void addManager(Map<String, AuthenticationManager> authenticationManagers, String issuer) {
        JwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);

        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        authenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
        authenticationManagers.put(issuer, authenticationProvider::authenticate);
    }


}

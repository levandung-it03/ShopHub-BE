package com.shophub.rest.config;

import com.shophub.rest.config.filter.CookiesSupportFilter;
import com.shophub.rest.config.filter.JWTAuthFilter;
import com.shophub.rest.util.URest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.shophub.rest.util.contants.CCommon.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CookiesSupportFilter cookiesFilter;
    private final JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors(cors -> cors.configurationSource(this.configurationSource()))
            .csrf(AbstractHttpConfigurer::disable)  // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) for Gateway
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                .requestMatchers(API.SECURE + ROLE.ADMIN + API.ALL_PATH).hasAuthority(ROLE.ADMIN)
                .requestMatchers(API.SECURE + ROLE.USER + API.ALL_PATH).hasAuthority(ROLE.USER)
                .requestMatchers(API.SECURE + ROLE.AUTH + API.ALL_PATH).hasAnyAuthority(ROLE.ADMIN, ROLE.USER)
                .requestMatchers(API.PUBLIC + API.ALL_PATH).permitAll()
//                .requestMatchers("/api/test/**").permitAll()
//                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .anyRequest().denyAll()
            )
            //--Role's denied from @PreAuthorize, and ExceptionTranslationFilter will throw error (caught at this point).
            .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler()))
            //--UsernamePasswordAuthenticationFilter is the first filter in SecurityFilterChain Collection.
            .addFilterBefore(cookiesFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(jwtAuthFilter, CookiesSupportFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return URest.Security::handleAccessDenied;
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(Symbols.ASTERISK));
        config.setAllowedMethods(List.of(API.POST, API.OPTIONS, API.GET, API.DELETE, API.PUT));
        config.setAllowedHeaders(List.of(ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION));
        config.setAllowCredentials(true);   //--'allowedOrigins' can not be '*' if this field is 'true'.
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(API.ALL_PATH, config);
        return source;
    }
}

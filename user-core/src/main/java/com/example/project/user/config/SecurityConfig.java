package com.example.project.user.config;

import com.example.project.user.security.JwtAccessDeniedHandler;
import com.example.project.user.security.JwtAuthenticationEntryPoint;
import com.example.project.user.security.JwtFilter;
import com.example.project.user.security.TokenProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenProvider tokenProvider;

    @Value("#{'${cors.allow-origins}'.split(',')}")
    private List<String> allowOrigins;

    private final Environment environment;

    private static final String[] DEV_WHITELIST = {
            "/v3/api-docs/**",
            "/actuator/*",
            "/api/swagger-ui/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs.yaml",
            "/v3/api-docs.yaml"
    };

    private String[] whiteList = new String[0];

    @PostConstruct
    void init() {
        if(allowOrigins.isEmpty()) {
            throw new RuntimeException("allow origins is empty");
        }

//        if(!environment.matchesProfiles("prod")) {
//            whiteList = DEV_WHITELIST;
//        }
        whiteList = DEV_WHITELIST;

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                    exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler);
                })
                .authorizeHttpRequests(request-> request
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/book/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtFilter(tokenProvider),  UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())).build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        List<String> allowWildcard = new ArrayList<>() {{
            add("*");
        }};
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowOrigins);
        configuration.setAllowedMethods(allowWildcard);
        configuration.setAllowedHeaders(allowWildcard);
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

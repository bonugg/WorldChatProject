package com.example.WorldChatProject.user.security;

import com.example.WorldChatProject.user.repository.UserRepository;
import com.example.WorldChatProject.user.security.jwt.JwtAuthenticationFilter;
import com.example.WorldChatProject.user.security.jwt.JwtAuthorizationFilter;
import com.example.WorldChatProject.user.security.repository.RefreshTokenRepository;
import com.example.WorldChatProject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    @Autowired
    private CorsConfig corsConfig;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .disable()
                )
                .addFilter(corsConfig.corsFilter())
                //세션 설정
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(formLogin -> formLogin
                        .disable()
                )
                .httpBasic(httpBasic -> httpBasic
                        .disable()
                )
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), refreshTokenRepository))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository, refreshTokenRepository))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers("/api/v1/user/**").hasRole("USER")
                                .requestMatchers("/friends/**").hasRole("USER")
                                .requestMatchers("/chat/**").hasRole("USER")
                                .requestMatchers("/api/v1/cateChat/**").hasRole("USER")
                                .requestMatchers("/random/room").hasRole("USER")
//                                .requestMatchers("/CateChat/**").hasRole("USER")
                                .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }
}
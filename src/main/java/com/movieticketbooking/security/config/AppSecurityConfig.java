package com.movieticketbooking.security.config;

import com.movieticketbooking.security.exception.CustomAccessDeniedHandler;
import com.movieticketbooking.security.exception.CustomBasicAuthenticationEntryPoint;
import com.movieticketbooking.security.jwt.JWTValidatorFilter;
import com.movieticketbooking.security.jwt.JwtUtils;
import com.movieticketbooking.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class AppSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers( HttpMethod.GET, "/api/users/me", "/api/movies").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/movies").hasRole("ADMIN")
                        .requestMatchers("/error", "/api/auth/**").permitAll())
                .formLogin(withDefaults())
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()))
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JWTValidatorFilter(jwtUtils, userDetailsService), BasicAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker getCompromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

}

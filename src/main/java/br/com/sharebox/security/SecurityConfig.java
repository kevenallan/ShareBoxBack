package br.com.sharebox.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenInterceptorFilter tokenInterceptorFilter;

    public SecurityConfig(TokenInterceptorFilter headerPrintFilter) {
        this.tokenInterceptorFilter = headerPrintFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200")); // Permita a origem desejada
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/usuario/login", "/register", "/public/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(tokenInterceptorFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    
}

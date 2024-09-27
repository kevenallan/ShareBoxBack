package br.com.sharebox.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> 
                authorize
                    .requestMatchers("/usuario/login", "/cadastrar").permitAll()  // Permitir login e registro
                    .anyRequest().authenticated()  // Todas as outras requisições precisam de autenticação
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Permitir credenciais, se necessário
//        config.addAllowedOrigin("http://192.168.0.5:4200"); // Origem permitida
        config.addAllowedOrigin("http://localhost:4200"); // Origem permitida
        config.addAllowedHeader("*"); // Todos os cabeçalhos são permitidos
        config.addAllowedMethod("GET"); // Permitir GET
        config.addAllowedMethod("POST"); // Permitir POST
        config.addAllowedMethod("PUT"); // Permitir PUT
        config.addAllowedMethod("DELETE"); // Permitir DELETE
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.addAllowedMethod("OPTIONS"); // Permitir OPTIONS
        source.registerCorsConfiguration("/**", config); // Aplicar para todos os endpoints
        return new CorsFilter(source);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

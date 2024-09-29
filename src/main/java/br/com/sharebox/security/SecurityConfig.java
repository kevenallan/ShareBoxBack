package br.com.sharebox.security;

//import java.util.List;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final TokenInterceptorFilter tokenInterceptorFilter;
//
//    public SecurityConfig(TokenInterceptorFilter headerPrintFilter) {
//        this.tokenInterceptorFilter = headerPrintFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .cors(cors -> cors.configurationSource(request -> {
//                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
//                corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200")); // Permita a origem desejada
//                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//                corsConfiguration.setAllowedHeaders(List.of("*"));
//                corsConfiguration.setAllowCredentials(true);
//                return corsConfiguration;
//            }))
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/usuario/login", "/register", "/public/**").permitAll() // Permite acesso sem autenticação
//                .requestMatchers("/arquivo/listar").authenticated() // Permite acesso a /arquivo/listar somente para usuários autenticados
//                .anyRequest().authenticated()) // Requer autenticação para qualquer outra requisição
//            .addFilterBefore(tokenInterceptorFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}

//import java.util.Arrays;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    private final TokenInterceptorFilter tokenInterceptorFilter;
//    public SecurityConfig(TokenInterceptorFilter tokenInterceptorFilter) {
//        this.tokenInterceptorFilter = tokenInterceptorFilter;
//    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
////            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authorizeHttpRequests(authorize -> 
//                authorize
//                    .requestMatchers("/usuario/login", "/cadastrar").permitAll()  // Permitir login e registro
//                    .anyRequest().authenticated()  // Todas as outras requisições precisam de autenticação
//            );
//        http.addFilterBefore(tokenInterceptorFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//    
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true); // Permitir credenciais, se necessário
////        config.addAllowedOrigin("http://192.168.0.5:4200"); // Origem permitida
//        config.addAllowedOrigin("http://localhost:4200"); // Origem permitida
//        config.addAllowedHeader("*"); // Todos os cabeçalhos são permitidos
//        config.addAllowedMethod("GET"); // Permitir GET
//        config.addAllowedMethod("POST"); // Permitir POST
//        config.addAllowedMethod("PUT"); // Permitir PUT
//        config.addAllowedMethod("DELETE"); // Permitir DELETE
//        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//        config.addAllowedMethod("OPTIONS"); // Permitir OPTIONS
//        source.registerCorsConfiguration("/**", config); // Aplicar para todos os endpoints
//        return new CorsFilter(source);
//    }
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//}


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenInterceptorFilter tokenInterceptorFilter;

    public SecurityConfig(TokenInterceptorFilter tokenInterceptorFilter) {
        this.tokenInterceptorFilter = tokenInterceptorFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200")); // Permita a origem desejada
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }))
            .authorizeHttpRequests(auth -> 
                auth
                    .requestMatchers("/usuario/login", "/usuario/cadastrar", "/usuario/esqueceu-sua-senha").permitAll() // Permite acesso sem autenticação
                    .anyRequest().authenticated() // Requer autenticação para qualquer outra requisição
            )
            .addFilterBefore(tokenInterceptorFilter, UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro de token

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Fornece um gerenciador de autenticação
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200"); // Origem permitida
        config.addAllowedHeader("*"); // Todos os cabeçalhos são permitidos
        config.addAllowedMethod("GET"); // Permitir GET
        config.addAllowedMethod("POST"); // Permitir POST
        config.addAllowedMethod("PUT"); // Permitir PUT
        config.addAllowedMethod("DELETE"); // Permitir DELETE
        config.addAllowedMethod("OPTIONS"); // Permitir OPTIONS
        source.registerCorsConfiguration("/**", config); // Aplicar para todos os endpoints
        return new CorsFilter(source);
    }
}


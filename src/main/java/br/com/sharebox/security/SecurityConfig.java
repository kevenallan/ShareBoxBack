package br.com.sharebox.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${cors.allowed-origins}")
	private String allowedOrigins;

	private final TokenInterceptorFilter tokenInterceptorFilter;

	public SecurityConfig(TokenInterceptorFilter tokenInterceptorFilter) {
		this.tokenInterceptorFilter = tokenInterceptorFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(request -> {
			var corsConfiguration = new CorsConfiguration();
			corsConfiguration.setAllowedOrigins(List.of(allowedOrigins.split(","))); // Permita a origem desejada --
																						// "http://localhost:4200",
																						// "http://192.168.0.5:4200",
																						// "https://sharebox-1155c.web.app"
			corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			corsConfiguration.setAllowedHeaders(List.of("*"));
			corsConfiguration.setAllowCredentials(true);
			return corsConfiguration;
		})).authorizeHttpRequests(auth -> auth
				.requestMatchers("/usuario/login", "/usuario/cadastrar", "/usuario/esqueceu-sua-senha",
						"/usuario/alterar-senha", "/firebase/get-config", "/usuario/login-google",
						"/arquivo/download-link")
				.permitAll() // Permite acesso sem autenticação
				.anyRequest().authenticated() // Requer autenticação para qualquer outra requisição
		).addFilterBefore(tokenInterceptorFilter, UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro de
																								// token

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager(); // Fornece um gerenciador de autenticação
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}

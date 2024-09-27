package br.com.sharebox.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenInterceptorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Captura o valor do header "Authorization" (ou qualquer outro)
        String headerValue = request.getHeader("Authorization");
        
        // Printar o valor do header no console
        System.out.println("Header Authorization: " + headerValue);

        // Continue com o restante da cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
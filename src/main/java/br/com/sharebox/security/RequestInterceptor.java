//package br.com.sharebox.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import br.com.sharebox.service.AuthService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class RequestInterceptor implements HandlerInterceptor {
//
//	@Autowired
//	private AuthService authService;
//	
//	@Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String authorizationHeader = request.getHeader("Authorization");
//        String requestURI = request.getRequestURI();
//
//        System.out.println("==========================================");
//        System.out.println("Authorization Header: " + authorizationHeader);
//        System.out.println("requestURI: " + requestURI);
//        System.out.println("==========================================");
//
//        if (requestURI.equals("/sharebox/usuario/login")) {
//        	return true;
//        }
//        
//        // Verifica se o cabeçalho Authorization existe e contém um token
//        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//            System.out.println("Token ausente ou malformado.");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
//            return false;
//        }
//
//        // Extrai o token do cabeçalho
//        String token = authorizationHeader.substring(7); // Remove "Bearer "
//
//        try {
//            // Valida o token
//            this.authService.validateToken(token);
//
//            // Permitir que a requisição continue
//            return true;
//
//        } catch (Exception e) {
//            System.out.println("Erro ao validar o token: " + e.getMessage());
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
//            return false;
//        }
//    }
//}

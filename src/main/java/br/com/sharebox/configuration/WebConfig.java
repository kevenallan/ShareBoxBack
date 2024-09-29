//package br.com.sharebox.configuration;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import br.com.sharebox.security.RequestInterceptor;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
////	@Autowired
////    private RequestInterceptor requestInterceptor;
//	
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // Permite todas as rotas
//                .allowedOrigins("http://localhost:4200", "http://192.168.0.5:4200") // Permite o front-end Angular
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
//                .allowedHeaders("*") // Todos os headers permitidos
//                .allowCredentials(true);
//    }
//    
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new RequestInterceptor());
//    }
//}
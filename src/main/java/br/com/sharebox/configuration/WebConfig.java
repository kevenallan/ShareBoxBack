package br.com.sharebox.configuration;

//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // Permite todas as rotas
//                .allowedOrigins("http://localhost:4200", "http://192.168.0.5:4200") // Permite o front-end Angular
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
//                .allowedHeaders("*") // Todos os headers permitidos
//                .allowCredentials(true);
//    }
//}
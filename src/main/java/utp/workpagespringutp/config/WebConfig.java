package utp.workpagespringutp.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar rutas para archivos estáticos
        registry.addResourceHandler("/IMG/**")
                .addResourceLocations("classpath:/static/IMG/");
        
        registry.addResourceHandler("/css/**", "/js/**", "/assets/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirigir todas las rutas que no coincidan con patrones de API o archivos estáticos a index.html
        // Esto permite que Angular router maneje la navegación
        registry.addViewController("/{spring:^(?!api|IMG|css|js|assets).*$}")
                .setViewName("forward:/index.html");
    }
}

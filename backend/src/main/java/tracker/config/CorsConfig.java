package tracker.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**") // Allows all paths/controllers in your app
                        .allowedOrigins("http://localhost:5173") // URL of your frontend app
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Allowed HTTP methods
                        .allowedHeaders("*") // Allows all headers (Authorization, Content-Type, etc.)
                        .allowCredentials(true); // Allows cookies or auth headers if needed
            }
        };
    }
}
package djnd.ben1607.drink_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    // everyone access
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/",
                "/storage/**",
                "/api/v1/reviews/**",
                "/api/v1/files/**",
                "/api/v1/images/**",
                "/api/v1/auth/**",
                "/api/v1/cart/**",
                "/api/v1/orders/**",
                "/api/v1/checkout",
                "/api/v1/users/**",
                "/api/v1/files/upload/**",
                "/api/v1/books",
                "/api/v1/categories",
                "/api/v1/payment/**"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}

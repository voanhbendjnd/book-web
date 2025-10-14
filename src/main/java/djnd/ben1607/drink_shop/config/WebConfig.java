package djnd.ben1607.drink_shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DelayInterceptor delayInterceptor;

    @Autowired
    private SecurityHeadersConfig.SecurityHeadersFilter securityHeadersFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(delayInterceptor)
                .addPathPatterns("/**"); // áp dụng cho tất cả API
    }
}
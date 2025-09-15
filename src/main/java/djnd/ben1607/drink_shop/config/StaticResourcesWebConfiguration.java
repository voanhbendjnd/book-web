package djnd.ben1607.drink_shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// set up file upload
@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {
    @Value("${djnd.upload-file.base-uri}")
    private String baseURI;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**").addResourceLocations(baseURI);
    }
}

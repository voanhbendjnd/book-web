package djnd.ben1607.drink_shop.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    /**
     * ✅ Register Security Headers Filter
     * Thứ tự cao để áp dụng security headers sớm nhất
     */
    @Bean
    public FilterRegistrationBean<SecurityHeadersConfig.SecurityHeadersFilter> securityHeadersFilter(
            SecurityHeadersConfig.SecurityHeadersFilter filter) {
        FilterRegistrationBean<SecurityHeadersConfig.SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // Highest priority
        registrationBean.setName("SecurityHeadersFilter");
        return registrationBean;
    }

    // @Bean
    // public FilterRegistrationBean<DelayFilter> loggingFilter() {
    // FilterRegistrationBean<DelayFilter> registrationBean = new
    // FilterRegistrationBean<>();
    // registrationBean.setFilter(new DelayFilter());
    // registrationBean.addUrlPatterns("/*");
    // return registrationBean;
    // }
}
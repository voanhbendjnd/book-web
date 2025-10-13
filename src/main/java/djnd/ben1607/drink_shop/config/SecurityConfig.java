package djnd.ben1607.drink_shop.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import djnd.ben1607.drink_shop.utils.SecurityUtils;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Value("${djnd.jwt.base64-secret}")
    private String jwtKey;

    @Bean // ghi de cau hinh mac dinh(override configuration defaut)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            SmartAuthenticationEntryPoint smartAuthenticationEntryPoint,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) throws Exception {

        String[] whiteList = {
                "/",
                "/api/v1/auth/**",
                "/api/v1/auth/login",
                "/api/v1/auth/refresh",
                "/api/v1/auth/register",
                "/api/v1/auth/get-otp",
                "/api/v1/auth/change-password-by-otp",
                "/storage/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/storage/**",
                "/api/v1/images/**",
                // PUBLIC APIs - Không cần authentication
                "/api/v1/books/**",
                "/api/v1/categories/**"
        };
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // cors
                .csrf(c -> c.disable()) // co che bao ve (tắt csrf vì sử dụng jwt token)
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(whiteList)
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/input-otp").permitAll()
                                .requestMatchers(HttpMethod.GET, "/books").permitAll()
                                .requestMatchers("/oauth2/**", "/login/oauth2/code/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/forget-password").permitAll()
                                .anyRequest().authenticated() // tất cả request khác không có thì buộc phải có qua
                                                              // authentication

                )
                // tách bearer token
                // cấu hình jwt
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(smartAuthenticationEntryPoint))
                .formLogin(f -> f.disable()) // xoa form login
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }

    // đinh hướng cho filter dùng được bảo vệ api -> tách token
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtils.JWT_ALGORITHM).build();
        // Giải mã token thành công trả về jwt không thành công trả về exception
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                throw ex;
            }

        };

    }

    // Phân quyền cho permission với Single Session validation
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(CustomJwtAuthenticationConverter customConverter) {
        // Cấu hình CustomJwtAuthenticationConverter để validate session
        customConverter.setAuthorityPrefix(""); // Không có prefix cho authorities
        customConverter.setAuthoritiesClaimName("permission"); // Tên claim chứa permissions trong JWT

        // Tạo JWT Authentication Converter với custom converter
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(customConverter);

        return jwtAuthenticationConverter;
    }

    // tạo encoder
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                SecurityUtils.JWT_ALGORITHM.getName());
    }
}

package djnd.ben1607.drink_shop.config;

import org.springframework.context.annotation.Configuration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security Headers Configuration
 * Bảo vệ ứng dụng khỏi các cuộc tấn công phổ biến
 */
@Configuration
public class SecurityHeadersConfig {

    /**
     * Custom Security Headers Filter
     * Thêm các headers bảo mật vào mọi response
     */
    @Component
    public static class SecurityHeadersFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {

            // ✅ X-Frame-Options: Ngăn chặn clickjacking
            response.setHeader("X-Frame-Options", "DENY");

            // ✅ X-Content-Type-Options: Ngăn MIME type sniffing
            response.setHeader("X-Content-Type-Options", "nosniff");

            // ✅ X-XSS-Protection: Bảo vệ khỏi XSS attacks
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // ✅ Referrer-Policy: Kiểm soát thông tin referrer
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // ✅ X-Permitted-Cross-Domain-Policies: Ngăn Adobe Flash/PDF attacks
            response.setHeader("X-Permitted-Cross-Domain-Policies", "none");

            // ✅ Permissions-Policy: Kiểm soát browser features
            response.setHeader("Permissions-Policy",
                    "geolocation=(), microphone=(), camera=(), payment=(), usb=(), magnetometer=(), gyroscope=(), speaker=(), vibrate=(), fullscreen=(self), sync-xhr=()");

            // ✅ Content-Security-Policy: Ngăn XSS và injection attacks
            response.setHeader("Content-Security-Policy",
                    "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data: https:; " +
                            "font-src 'self' data:; " +
                            "connect-src 'self'; " +
                            "frame-ancestors 'none'; " +
                            "base-uri 'self'; " +
                            "form-action 'self'");

            // ✅ Strict-Transport-Security: Force HTTPS (chỉ khi HTTPS)
            if (request.isSecure()) {
                response.setHeader("Strict-Transport-Security",
                        "max-age=31536000; includeSubDomains; preload");
            }

            // ✅ Cache-Control: Kiểm soát caching cho sensitive data
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/api/v1/auth/") ||
                    requestURI.startsWith("/api/v1/admin/") ||
                    requestURI.contains("user")) {
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
            }

            filterChain.doFilter(request, response);
        }
    }
}

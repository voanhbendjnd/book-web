package djnd.ben1607.drink_shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiting Configuration
 * Bảo vệ API khỏi brute force và DoS attacks
 */
@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitingInterceptor())
                .addPathPatterns("/api/v1/auth/**") // Áp dụng cho auth endpoints
                .addPathPatterns("/api/v1/admin/**"); // Áp dụng cho admin endpoints
    }

    @Component
    public static class RateLimitingInterceptor implements HandlerInterceptor {

        // ✅ Rate limiting storage
        private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

        // ✅ Rate limiting rules
        private static final int MAX_REQUESTS_PER_MINUTE = 60;
        private static final int MAX_REQUESTS_PER_HOUR = 1000;
        private static final int MAX_LOGIN_ATTEMPTS = 5; // Per 15 minutes
        private static final long CLEANUP_INTERVAL = TimeUnit.MINUTES.toMillis(5);

        @Override
        public boolean preHandle(HttpServletRequest request,
                HttpServletResponse response,
                Object handler) throws Exception {

            String clientIp = getClientIpAddress(request);
            String endpoint = request.getRequestURI();

            // ✅ Cleanup old entries periodically
            cleanupOldEntries();

            // ✅ Check rate limits
            if (!checkRateLimit(clientIp, endpoint)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\":\"Too many requests. Please try again later.\"," +
                                "\"statusCode\":429," +
                                "\"message\":\"Rate limit exceeded\"}");
                return false;
            }

            return true;
        }

        private boolean checkRateLimit(String clientIp, String endpoint) {
            long currentTime = System.currentTimeMillis();
            String key = clientIp + ":" + endpoint;

            RateLimitInfo info = rateLimitMap.computeIfAbsent(key,
                    k -> new RateLimitInfo());

            // ✅ Check per-minute limit
            if (info.getMinuteCount().get() >= MAX_REQUESTS_PER_MINUTE) {
                return false;
            }

            // ✅ Check per-hour limit
            if (info.getHourCount().get() >= MAX_REQUESTS_PER_HOUR) {
                return false;
            }

            // ✅ Check login attempts limit
            if (endpoint.contains("/auth/login") &&
                    info.getLoginAttempts().get() >= MAX_LOGIN_ATTEMPTS) {
                return false;
            }

            // ✅ Update counters
            info.getMinuteCount().incrementAndGet();
            info.getHourCount().incrementAndGet();

            if (endpoint.contains("/auth/login")) {
                info.getLoginAttempts().incrementAndGet();
            }

            // ✅ Reset counters based on time windows
            resetCountersIfNeeded(info, currentTime);

            return true;
        }

        private void resetCountersIfNeeded(RateLimitInfo info, long currentTime) {
            // Reset minute counter every minute
            if (currentTime - info.getLastMinuteReset() > TimeUnit.MINUTES.toMillis(1)) {
                info.getMinuteCount().set(0);
                info.setLastMinuteReset(currentTime);
            }

            // Reset hour counter every hour
            if (currentTime - info.getLastHourReset() > TimeUnit.HOURS.toMillis(1)) {
                info.getHourCount().set(0);
                info.setLastHourReset(currentTime);
            }

            // Reset login attempts every 15 minutes
            if (currentTime - info.getLastLoginReset() > TimeUnit.MINUTES.toMillis(15)) {
                info.getLoginAttempts().set(0);
                info.setLastLoginReset(currentTime);
            }
        }

        private void cleanupOldEntries() {
            long currentTime = System.currentTimeMillis();
            rateLimitMap.entrySet()
                    .removeIf(entry -> currentTime - entry.getValue().getLastAccess() > TimeUnit.HOURS.toMillis(1));
        }

        private String getClientIpAddress(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            return request.getRemoteAddr();
        }

        // ✅ Rate limit info class
        private static class RateLimitInfo {
            private final AtomicInteger minuteCount = new AtomicInteger(0);
            private final AtomicInteger hourCount = new AtomicInteger(0);
            private final AtomicInteger loginAttempts = new AtomicInteger(0);
            private long lastMinuteReset = System.currentTimeMillis();
            private long lastHourReset = System.currentTimeMillis();
            private long lastLoginReset = System.currentTimeMillis();
            private long lastAccess = System.currentTimeMillis();

            // Getters and setters
            public AtomicInteger getMinuteCount() {
                return minuteCount;
            }

            public AtomicInteger getHourCount() {
                return hourCount;
            }

            public AtomicInteger getLoginAttempts() {
                return loginAttempts;
            }

            public long getLastMinuteReset() {
                return lastMinuteReset;
            }

            public void setLastMinuteReset(long lastMinuteReset) {
                this.lastMinuteReset = lastMinuteReset;
            }

            public long getLastHourReset() {
                return lastHourReset;
            }

            public void setLastHourReset(long lastHourReset) {
                this.lastHourReset = lastHourReset;
            }

            public long getLastLoginReset() {
                return lastLoginReset;
            }

            public void setLastLoginReset(long lastLoginReset) {
                this.lastLoginReset = lastLoginReset;
            }

            public long getLastAccess() {
                return lastAccess;
            }

            public void setLastAccess(long lastAccess) {
                this.lastAccess = lastAccess;
            }
        }
    }
}

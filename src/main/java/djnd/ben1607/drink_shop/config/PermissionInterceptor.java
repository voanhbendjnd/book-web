package djnd.ben1607.drink_shop.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import djnd.ben1607.drink_shop.domain.entity.Permission;
import djnd.ben1607.drink_shop.domain.entity.Role;
import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.service.UserService;
import djnd.ben1607.drink_shop.utils.SecurityUtils;
import djnd.ben1607.drink_shop.utils.error.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Transactional
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    // @SuppressWarnings({ "null", "unused" })
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        String email = SecurityUtils.getCurrentUserLogin().isPresent()
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";
        if (!email.isEmpty()) {
            // Optimized: Use cached user lookup + cached permission check
            User user = this.userService.fetchUserByEmail(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    // Check permission using cached method
                    boolean hasPermission = checkUserPermission(email, path, httpMethod);
                    if (!hasPermission) {
                        throw new PermissionException("You do not have permission to access this endpoint!!!");
                    }
                } else {
                    throw new PermissionException("You do not have permission to access this endpoint!!!");
                }
            }
        }

        return true;
    }

    /**
     * Cached permission checking method
     * Cache key: "permissions::email_path_method"
     * Example: "permissions::john@email.com_/api/users_GET"
     */
    @Cacheable(value = "permissions", key = "#email + '_' + #path + '_' + #method")
    public boolean checkUserPermission(String email, String path, String method) {
        User user = this.userService.fetchUserByEmail(email);
        if (user == null || user.getRole() == null) {
            return false;
        }

        List<Permission> permissions = user.getRole().getPermissions();
        return permissions.stream().anyMatch(
                permission -> permission.getApiPath().equals(path)
                        && permission.getMethod().equals(method));
    }
}

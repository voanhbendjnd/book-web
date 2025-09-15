package djnd.ben1607.drink_shop.config;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import djnd.ben1607.drink_shop.service.UserService;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Component("userDetailsService")
public class UserDetailCustoms implements UserDetailsService {
    private final UserService userService;

    public UserDetailCustoms(UserService userService) {
        this.userService = userService;
    }

    // principal
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        djnd.ben1607.drink_shop.domain.entity.User user = this.userService.fetchUserByEmail(username);
        if (user == null)
            throw new UsernameNotFoundException("Username/password not found or exactly");
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())));
    }
}
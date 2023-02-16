package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.user.entity.enums.Role;
import co.kurrant.app.admin_api.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!username.equals("admin")){
            throw new UsernameNotFoundException(username);
        }
        return new SecurityUser(username, "15779612", Role.ADMIN);
    }
}

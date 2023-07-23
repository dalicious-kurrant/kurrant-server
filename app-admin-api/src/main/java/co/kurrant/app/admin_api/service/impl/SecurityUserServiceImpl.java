package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.repository.DriverRepository;
import co.dalicious.domain.user.entity.enums.Role;
import co.kurrant.app.admin_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements UserDetailsService {
    private final DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.equals("admin")){
            return new SecurityUser(username, "15779612", Role.ADMIN);
        }
        Driver driver = driverRepository.findByName(username)
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
        if(driver.getName().equals(username)) {
            return new SecurityUser(username, "15779612", Role.USER);
        }
        throw new ApiException(ExceptionEnum.UNAUTHORIZED);
    }
}

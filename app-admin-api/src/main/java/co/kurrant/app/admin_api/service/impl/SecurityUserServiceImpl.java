package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.user.entity.enums.Role;
import co.kurrant.app.admin_api.dto.Code;
import co.kurrant.app.admin_api.model.SecurityUser;
import co.kurrant.app.admin_api.util.DeliveryCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements UserDetailsService {
    private final DeliveryCodeUtil deliveryCodeUtil;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Code> codes = deliveryCodeUtil.getEntireDeliveryCodes();
        if(codes.stream().map(Code::getCode).toList().contains(username)) {
            return new SecurityUser(username, "15779612", Role.USER);
        }
        if(!username.equals("admin")){
            throw new UsernameNotFoundException(username);
        }
        return new SecurityUser(username, "15779612", Role.ADMIN);
    }
}

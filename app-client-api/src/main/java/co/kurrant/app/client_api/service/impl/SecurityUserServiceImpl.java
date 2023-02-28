package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.domain.user.entity.enums.Role;
import co.kurrant.app.client_api.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements UserDetailsService {

    private final CorporationRepository groupRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Corporation group = groupRepository.findByCode(username);
        if(group == null){
            throw new UsernameNotFoundException(username);
        }
        return new SecurityUser(group.getId(), group.getName(), Role.USER);
    }
}

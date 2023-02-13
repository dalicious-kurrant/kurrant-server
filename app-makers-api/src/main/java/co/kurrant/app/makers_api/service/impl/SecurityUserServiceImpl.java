package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.kurrant.app.makers_api.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements UserDetailsService {

    private final MakersRepository makersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Makers makers = makersRepository.findByCode(username);
        if(makers == null){
            throw new UsernameNotFoundException(username);
        }
        return new SecurityUser(makers.getId(), makers.getName(), makers.getPassword(), makers.getCode(), makers.getRole());
    }
}

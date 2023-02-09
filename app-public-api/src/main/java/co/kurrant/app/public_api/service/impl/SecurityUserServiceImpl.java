package co.kurrant.app.public_api.service.impl;

import java.math.BigInteger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.DomainUserRepository;
import co.kurrant.app.public_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements UserDetailsService {
  private final DomainUserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    BigInteger userPk = BigInteger.valueOf(Integer.parseInt(id));
    User user = userRepository.findById(userPk)
        .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

    return SecurityUser.builder().id(user.getId()).name(user.getName()).role(user.getRole())
        .email(user.getEmail()).build();
  }
}

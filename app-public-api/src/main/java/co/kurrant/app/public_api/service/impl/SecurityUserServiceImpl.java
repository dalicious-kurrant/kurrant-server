package co.kurrant.app.public_api.service.impl;

import co.kurrant.app.public_api.model.UserAccount;
import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        BigInteger userPk = BigInteger.valueOf(Integer.parseInt(id));
        User user = userRepository.findById(userPk).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND)
        );

        return new UserAccount(user);
    }

//    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
//        BigInteger userPk = BigInteger.valueOf(Integer.parseInt(id));
//        User user = userRepository.findById(userPk).orElseThrow(
//                () -> new ApiException(ExceptionEnum.NOT_FOUND)
//        );
//
//        return new UserAccount(user);
//    }
}

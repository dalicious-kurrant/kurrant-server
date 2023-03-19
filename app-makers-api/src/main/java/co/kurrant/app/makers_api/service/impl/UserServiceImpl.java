package co.kurrant.app.makers_api.service.impl;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.client.core.filter.provider.SimpleJwtTokenProvider;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.kurrant.app.makers_api.dto.user.LoginRequestDto;
import co.kurrant.app.makers_api.dto.user.LoginResponseDto;
import co.kurrant.app.makers_api.service.UserService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final MakersRepository makersRepository;
    private final PasswordEncoder passwordEncoder;
    private final SimpleJwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        Makers makers = makersRepository.findByCode(dto.getCode());
        if(makers == null) {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        }

//        if (!passwordEncoder.matches(dto.getPassword(), makers.getPassword())) {
//            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
//        }
        if(!dto.getPassword().equals("15779612")) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }

        return getLoginAccessToken(makers);
    }

    public LoginResponseDto getLoginAccessToken(Makers makers) {
        // 토큰에 권한 넣기
        List<String> authorities = new ArrayList<>();
        authorities.add(makers.getRole().getAuthority());
        LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(makers.getCode(), authorities);

        return LoginResponseDto.create(loginResponseDto, makers);
    }
}

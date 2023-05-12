package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.client.core.filter.provider.SimpleJwtTokenProvider;
import co.kurrant.app.admin_api.dto.user.LoginRequestDto;
import co.kurrant.app.admin_api.dto.user.LoginResponseDto;
import co.kurrant.app.admin_api.model.Admin;
import co.kurrant.app.admin_api.service.AuthService;
import co.kurrant.app.admin_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthServiceImpl implements AuthService {
    private final SimpleJwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        //id check
        if (dto.getUsername() == null) {
            throw new ApiException(ExceptionEnum.NOT_INPUT_USERNAME);
        } else if (!dto.getUsername().equals("admin")) {
            throw new ApiException(ExceptionEnum.NOT_MATCHED_USERNAME);
        }
        //password check
        if (dto.getPassword() == null) {
            throw new ApiException(ExceptionEnum.NOT_INPUT_PASSWORD);
        } else if (!dto.getPassword().equals("15779612")) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }

        Admin admin = new Admin(dto.getUsername(), dto.getPassword());

        return getLoginAccessToken(admin);
    }

    public LoginResponseDto getLoginAccessToken(Admin admin) {
        // 토큰에 권한 넣기
        List<String> authorities = new ArrayList<>();
        authorities.add(admin.getRole().getAuthority());
        LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(admin.getUsername(), authorities);

        return LoginResponseDto.create(loginResponseDto, admin);
    }

}

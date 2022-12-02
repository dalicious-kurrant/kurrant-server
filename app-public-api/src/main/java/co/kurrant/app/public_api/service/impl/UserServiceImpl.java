package co.kurrant.app.public_api.service.impl;

import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.client.external.sms.SmsMessageDto;
import co.dalicious.client.external.sms.SmsService;
import co.dalicious.domain.user.dto.UserDto;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.ChangePasswordRequestDto;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.service.impl.mapper.UserMapper;
import co.kurrant.app.public_api.validator.UserValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CommonService commonService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;

    @Override
    public void changePhoneNumber(HttpServletRequest httpServletRequest, SmsMessageDto smsMessageDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        User user = commonService.getUser(httpServletRequest);
        userValidator.isPhoneValid(smsMessageDto.getTo());
    }

    @Override
    @Transactional
    public void changePassword(HttpServletRequest httpServletRequest, ChangePasswordRequestDto changePasswordRequestDto) {
        User user = commonService.getUser(httpServletRequest);
        // 현재 비밀번호가 일치하는지 확인
        if(!passwordEncoder.matches(changePasswordRequestDto.getCurrantPassword(), user.getPassword())) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        };
        // 새로 등록한 번호 두개가 일치하는 지 확인
        if(!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getNewPasswordCheck())) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }
        // 비밀번호 변경
        String hashedPassword = passwordEncoder.encode(changePasswordRequestDto.getNewPassword());
        user.changePassword(hashedPassword);
    }

    @Override
    public UserDto getUserInfo(HttpServletRequest httpServletRequest) {
        User user = commonService.getUser(httpServletRequest);
        return UserMapper.INSTANCE.toDto(user);
    }

}

package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.user.entity.Provider;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.kurrant.app.public_api.dto.user.ChangePhoneRequestDto;
import co.kurrant.app.public_api.dto.user.SetEmailAndPasswordDto;
import co.kurrant.app.public_api.dto.user.UserInfoDto;
import co.kurrant.app.public_api.service.impl.mapper.UserInfoMapper;
import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.client.external.sms.SmsService;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.ChangePasswordRequestDto;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.UserService;
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
    private final ProviderEmailRepository providerEmailRepository;

    @Override
    @Transactional
    public void changePhoneNumber(HttpServletRequest httpServletRequest, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 로그인한 유저의 정보를 받아온다.
        User user = commonService.getUser(httpServletRequest);
        // 입력한 휴대폰 번호가 기존에 등록된 번호인지 확인한다.
        userValidator.isPhoneValid(changePhoneRequestDto.getPhone());
        // 인증번호가 일치하는지 확인한다.
        smsService.verifySms(changePhoneRequestDto.getKey());
        // 비밀번호를 업데이트 한다.
        user.changePhoneNumber(changePhoneRequestDto.getPhone());
    }

    @Override
    @Transactional
    public void changePassword(HttpServletRequest httpServletRequest, ChangePasswordRequestDto changePasswordRequestDto) {
        // 로그인한 유저의 정보를 받아온다.
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
    @Transactional
    public void setEmailAndPassword(HttpServletRequest httpServletRequest, SetEmailAndPasswordDto setEmailAndPasswordDto) {
        // 로그인한 유저의 정보를 받아온다.
        User user = commonService.getUser(httpServletRequest);

        // 기존에 존재하는 이메일인지 확인
        userValidator.isEmailValid(Provider.GENERAL, setEmailAndPasswordDto.getEmail());

        // 비밀번호 일치 확인
        String password = setEmailAndPasswordDto.getPassword();
        String passwordCheck = setEmailAndPasswordDto.getPasswordCheck();
        userValidator.isPasswordMatched(password, passwordCheck);

        // 이메일/비밀번호 업데이트
        String email = setEmailAndPasswordDto.getEmail();
        String hashedPassword = passwordEncoder.encode(password);
        user.setEmailAndPassword(email, hashedPassword);

        // 일반 로그인 저장
        ProviderEmail providerEmail = ProviderEmail.builder()
                .provider(Provider.GENERAL)
                .email(email)
                .user(user)
                .build();
        providerEmailRepository.save(providerEmail);
    }

    @Override
    public UserInfoDto getUserInfo(HttpServletRequest httpServletRequest) {
        User user = commonService.getUser(httpServletRequest);
        return UserInfoMapper.INSTANCE.toDto(user);
    }

}

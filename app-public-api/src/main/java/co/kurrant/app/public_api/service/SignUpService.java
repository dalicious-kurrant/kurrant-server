package co.kurrant.app.public_api.service;

import co.dalicious.client.core.exception.ApiException;
import co.dalicious.client.core.exception.ExceptionEnum;
import co.dalicious.domain.user.entity.Apartment;
import co.dalicious.domain.user.entity.Corporation;
import co.dalicious.domain.user.repository.ApartmentRepository;
import co.dalicious.domain.user.repository.CorporationRepository;
import co.dalicious.domain.user.entity.Role;
import co.kurrant.app.public_api.dto.user.SignUpRequestDto;
import co.kurrant.app.public_api.service.impl.mapper.UserMapper;
import co.kurrant.app.public_api.validation.UserValidation;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorporationRepository corporationRepository;
    private final ApartmentRepository apartmentRepository;
//    private static final SecureRandom random = new SecureRandom();
//    private static final int SALT_LENGTH = 64;

    @Transactional
    public User SignUp(SignUpRequestDto signUpRequestDto) {
        String password = signUpRequestDto.getPassword();

        // 비밀번호 일치 체크
        UserValidation.CheckIsPasswordMatched(password, signUpRequestDto.getPasswordCheck());

        // Hashed Password 생성
        String hashedPassword = passwordEncoder.encode(password);

        // N/A인 Corporation 과 Apartment 가져오기

        Corporation corporation = corporationRepository.findByName("N/A").orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND)
        );

        Apartment apartment = apartmentRepository.findByName("N/A").orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND)
        );

        UserDto userDto = UserDto.builder()
                .email(signUpRequestDto.getEmail())
                .phone(signUpRequestDto.getPhone())
                .password(hashedPassword)
                .name(signUpRequestDto.getName())
                .role(Role.USER)
                .apartment(apartment)
                .corporation(corporation)
                .build();
        // Corporation과 Apartment가 null로 대입되는 오류 발생
        User user = UserMapper.INSTANCE.toEntity(userDto);

        //User 저장
        return userRepository.save(user);
    }

//    static byte[] createSalt() {
//        byte[] salt = new byte[SALT_LENGTH];
//        random.nextBytes(salt);
//        return salt;
//    }
}

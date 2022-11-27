package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.dto.user.SignUpRequestDto;
import co.kurrant.app.public_api.service.impl.mapper.UserMapper;
import co.kurrant.app.public_api.validation.UserValidation;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private static final SecureRandom random = new SecureRandom();
//    private static final int SALT_LENGTH = 64;

    public User SignUp(SignUpRequestDto signUpRequestDto) {
        String password = signUpRequestDto.getPassword();

        // 비밀번호 일치 체크
        UserValidation.CheckIsPasswordMatched(password, signUpRequestDto.getPasswordCheck());

        //Hashed Password 생성
        String hashedPassword = passwordEncoder.encode(password);

        //N/A인 Corporation과 Apartment 가져오기


        //User 객체 생성
        UserDto userDto = UserDto.builder()
                .email(signUpRequestDto.getEmail())
                .phone(signUpRequestDto.getPhone())
                .password(hashedPassword)
                .name(signUpRequestDto.getName())
                .apartment()
                .corporation()
                .build();

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

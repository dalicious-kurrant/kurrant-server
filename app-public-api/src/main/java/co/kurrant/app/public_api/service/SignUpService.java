package co.kurrant.app.public_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final Pbkdf2PasswordEncoder passwordEncoder;

//    public User SignUp(SignUpRequestDto signUpRequestDto) {
//        String password = signUpRequestDto.getPassword();
//
//        // 비밀번호 일치 체크
//        CheckIsPasswordMatched(password, signUpRequestDto.getPasswordCheck());
//
//        byte[] hashedPassword = passwordEncoder.encode(password);
//    }
}

package co.kurrant.app.public_api.validator;

import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.domain.user.entity.Provider;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.dalicious.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class UserValidator {
    private final UserRepository userRepository;
    private final ProviderEmailRepository providerEmailRepository;
    public void isPasswordMatched(String password, String passwordCheck) {
        if(!password.equals(passwordCheck)) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }
    }

    public void isEmailValid(Provider provider, String email) {
        List<ProviderEmail> providerEmailList = providerEmailRepository.findAllByProviderAndEmail(provider, email);
        if(!providerEmailList.isEmpty()) {
            throw new ApiException(ExceptionEnum.ALREADY_EXISTING_USER);
        }
    }

    public void isPhoneValid(String phone) {
        Optional<User> user = userRepository.findByPhone(phone);
        if(user.isPresent()) {
            throw new ApiException(ExceptionEnum.ALREADY_EXISTING_USER);
        }
    }

    public void isValidPassword(String password) {
        String pattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,32}$";
        if(!Pattern.matches(pattern, password)) {
            throw new ApiException(ExceptionEnum.DOSE_NOT_SATISFY_PASSWORD_PATTERN_REQUIREMENT);
        }
    }

    public User getExistingUser(String email) {
        List<ProviderEmail> providerEmails = providerEmailRepository.findAllByEmail(email);
        if(!providerEmails.isEmpty()) {
            return providerEmails.get(0).getUser();
        } else {
            return null;
        }
    }
}

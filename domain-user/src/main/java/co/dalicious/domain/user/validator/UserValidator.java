package co.dalicious.domain.user.validator;

import co.dalicious.domain.user.entity.enums.Role;
import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.domain.user.entity.enums.Provider;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.dalicious.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class UserValidator {
    private final UserRepository userRepository;
    private final ProviderEmailRepository providerEmailRepository;
    public static void isPasswordMatched(String password, String passwordCheck) {
        if(!password.equals(passwordCheck)) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }
    }

    public void isEmailValid(Provider provider, String email) {
        Optional<ProviderEmail> providerEmail = providerEmailRepository.findOneByProviderAndEmail(provider, email);
        if(providerEmail.isPresent()) {
            throw new ApiException(ExceptionEnum.EXCEL_EMAIL_DUPLICATION);
        }
    }

    public void isEmailValid(User user, String email) {
        List<ProviderEmail> providerEmails = providerEmailRepository.findAllByEmail(email);
        for (ProviderEmail providerEmail : providerEmails) {
            if(!providerEmail.getUser().equals(user)) {
                throw new ApiException(ExceptionEnum.EXCEL_EMAIL_DUPLICATION);
            }
        }
    }

    public void isExistingMainEmail(String email) {
        if(userRepository.findOneByEmail(email).isPresent()) {
            throw new ApiException(ExceptionEnum.ALREADY_EXISTING_USER);
        };
    }

    public void isPhoneValid(String phone) {
        Optional<User> user = userRepository.findOneByPhone(phone);
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

    public static void isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(!m.matches()) {
            throw new ApiException(ExceptionEnum.NOT_VALID_EMAIL);
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

    public static Provider isValidProvider(String provider) {
        boolean isContainedSns = Arrays.toString(Provider.values()).contains(provider.toUpperCase());
        if(!isContainedSns) {
            throw new ApiException(ExceptionEnum.SNS_PLATFORM_NOT_FOUND);
        } else {
            return  Provider.valueOf(provider);
        }
    }

    public static void isAuthorizedUser(User user) {
        user.getProviderEmails().stream().filter(v -> v.getProvider().equals(Provider.GENERAL))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
    }

    public boolean adminExists() {
        return userRepository.existsByRole(Role.ADMIN);
    }

    public Boolean isPhoneValidBoolean(String phone) {
        Optional<User> user = userRepository.findOneByPhone(phone);
        return user.isPresent();
    }
}

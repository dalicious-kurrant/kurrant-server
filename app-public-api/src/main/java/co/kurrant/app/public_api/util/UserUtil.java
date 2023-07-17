package co.kurrant.app.public_api.util;

import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.public_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    // SecurityUser를 통해서 유저 객체 정보를 가져온다.
    public User getUser(SecurityUser securityUser) {
        return userRepository.findById(securityUser.getId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
    }
    // SecurityUser를 통해서 유저의 id를 가져온다.
    public BigInteger getUserId(SecurityUser securityUser) {
        return securityUser.getId();
    }

    public static SecurityUser securityUser(Authentication authentication) {
        if(authentication == null) {
            throw new ApiException(ExceptionEnum.ACCESS_TOKEN_ERROR);
        }
        return (SecurityUser) authentication.getPrincipal();
    }

    public User getUser(HttpServletRequest httpServletRequest) {
        // 토큰 가져오기
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        // 유효한 토큰인지 확인
        jwtTokenProvider.validateToken(token);
        // 유저 정보 가져오기
        BigInteger userId = BigInteger.valueOf(Integer.parseInt(jwtTokenProvider.getUserPk(token)));

        return userRepository.findById(userId).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
    }

    public static String generateRandomNickName() throws IOException {
        InputStream nounInputStream = UserUtil.class.getClassLoader().getResourceAsStream("nickname/noun.csv");
        BufferedReader nounReader = new BufferedReader(new InputStreamReader(nounInputStream));
        InputStream adjectiveInputStream = UserUtil.class.getClassLoader().getResourceAsStream("nickname/adjective.csv");
        BufferedReader adjectiveReader = new BufferedReader(new InputStreamReader(adjectiveInputStream));

        List<String> nouns = nounReader.lines().toList();
        List<String> adjectives = adjectiveReader.lines().toList();

        Random rand = new Random();

        String randomNoun = nouns.get(rand.nextInt(nouns.size()));
        String randomAdjective = adjectives.get(rand.nextInt(adjectives.size()));

        return randomAdjective + randomNoun;
    }

}

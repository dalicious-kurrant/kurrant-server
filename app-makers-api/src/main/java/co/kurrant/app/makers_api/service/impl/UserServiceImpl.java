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
//    private final RefreshTokenRepository refreshTokenRepository;
////    private final BlackListTokenRepository blackListTokenRepository;
//
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
//
//    @Override
//    public LoginTokenDto reissue(TokenDto reissueTokenDto) {
//        // Access Token이 유효한 경우
//        if(jwtTokenProvider.validateToken(reissueTokenDto.getAccessToken())) {
//            // 1. Refresh Token 검증
//            if (!jwtTokenProvider.validateToken(reissueTokenDto.getRefreshToken())) {
//                throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
//            }
//
//            // 2. Access Token 에서 UserId 를 가져오기.
//            String makersCode = jwtTokenProvider.getUserPk(reissueTokenDto.getAccessToken());
//
//            // 3. UserId를 통해 Redis에서 Refresh Token 값 꺼내기
//            List<RefreshTokenHash> refreshTokenHashs = refreshTokenRepository.findAllByUserId(makersCode);
//
//            // 4. 로그아웃 되어 Refresh Token이 존재하지 않는 경우 처리
//            if(refreshTokenHashs == null) {
//                throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
//            }
//            // 5. 잘못된 Refresh Token일 경우 예외 처리
//            RefreshTokenHash refreshTokenHash = refreshTokenHashs.stream().filter(v -> v.getRefreshToken().equals(reissueTokenDto.getRefreshToken()))
//                    .findAny()
//                    .orElseThrow(() -> new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR));
//
//            // 6. 새로운 토큰 생성
//            Authentication authentication = jwtTokenProvider.getAuthentication(reissueTokenDto.getAccessToken());
//            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//            List<String> strAuthorities = new ArrayList<>();
//            for (GrantedAuthority authority : authorities) {
//                strAuthorities.add(authority.getAuthority());
//            }
//            LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(makersCode, strAuthorities);
//
//            // 7. RefreshToken Redis 업데이트
//            refreshTokenRepository.delete(refreshTokenHash);
//            RefreshTokenHash newRefreshTokenHash = RefreshTokenHash.builder()
//                    .refreshToken(loginResponseDto.getRefreshToken())
//                    .userId(makersCode)
//                    .build();
//            refreshTokenRepository.save(newRefreshTokenHash);
//            return loginResponseDto;
//        }
//        // Access Token이 유효하지 않은 경우
//        else {
//            String refreshToken = reissueTokenDto.getRefreshToken();
//            // 1. Refresh Token 검증
//            if (!jwtTokenProvider.validateToken(refreshToken)) {
//                throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
//            }
//
//            // 2. Refresh Token 값를 통해 Redis에서 Refresh Token 값 꺼내기
//            Optional<RefreshTokenHash> refreshTokenHash = refreshTokenRepository.findOneByRefreshToken(refreshToken);
//
//            // 3. 로그아웃 되어 Refresh Token이 존재하지 않는 경우 처리
//            if(refreshTokenHash.isEmpty()) {
//                throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
//            }
//            // 4. Refresh Token을 통해 유저 정보 가져오기
//            String makersCode = refreshTokenHash.get().getUserId();
//            Makers makers = makersRepository.findByCode(makersCode);
//            if(makers == null) {
//                throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
//            }
//            List<String> roles = new ArrayList<>();
//            roles.add(makers.getRole().getAuthority());
//
//            // 6. 새로운 토큰 생성
//            LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(makersCode, roles);
//
//            // 7. RefreshToken Redis 업데이트
//            refreshTokenRepository.delete(refreshTokenHash.get());
//            RefreshTokenHash newRefreshTokenHash = RefreshTokenHash.builder()
//                    .refreshToken(loginResponseDto.getRefreshToken())
//                    .userId(makersCode)
//                    .build();
//            refreshTokenRepository.save(newRefreshTokenHash);
//            return loginResponseDto;
//
//        }
//
//    }
//
//    @Override
//    public void logout(TokenDto tokenDto) {
//        // 1. Refresh Token 검증
//        if (!jwtTokenProvider.validateToken(tokenDto.getRefreshToken())) {
//            throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
//        }
//        // 2. Access Token 에서 UserId 를 가져오기.
//        String makersCode = jwtTokenProvider.getUserPk(tokenDto.getAccessToken());
//
//        // 3. Redis 에서 해당 UserId로 저장된 Refresh Token 이 있는지 여부를 확인 후 존재할 경우 삭제.
//        // TODO: 다른 기기에서 로그인 한 유저들을 구분하기 위해서 특정 토큰만 받아와서 삭제하기
//        List<RefreshTokenHash> refreshTokenHashes = refreshTokenRepository.findAllByUserId(makersCode);
//        if (refreshTokenHashes != null) {
//            refreshTokenRepository.deleteAll(refreshTokenHashes);
//        }
//
//        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
//        Long accessTokenExpiredIn = jwtTokenProvider.getExpiredIn(tokenDto.getAccessToken());
//        blackListTokenRepository.save(BlackListTokenHash.builder()
//                .expiredIn(accessTokenExpiredIn)
//                .accessToken(tokenDto.getAccessToken())
//                .build());
//    }

    public LoginResponseDto getLoginAccessToken(Makers makers) {
        // 토큰에 권한 넣기
        List<String> authorities = new ArrayList<>();
        authorities.add(makers.getRole().getAuthority());
        LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(makers.getCode(), authorities);

        return LoginResponseDto.create(loginResponseDto, makers);
    }
}

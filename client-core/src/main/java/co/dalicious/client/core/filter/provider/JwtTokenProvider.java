package co.dalicious.client.core.filter.provider;

import java.math.BigInteger;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.client.core.entity.RefreshToken;
import co.dalicious.client.core.repository.RefreshTokenRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// JWT 토큰을 생성 및 검증 모듈
@Slf4j
@RequiredArgsConstructor
@Component
@PropertySource("classpath:application-jwt.properties")
public class JwtTokenProvider {
    private final RefreshTokenRepository refreshTokenRepository;

    private Key key;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final UserDetailsService userDetailsService;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 2 * 60 * 60 * 1000L; // 2시간
    // private static final long ACCESS_TOKEN_EXPIRE_TIME = 10 * 1000L; // 10초
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Jwt 토큰 생성
    public LoginTokenDto createToken(String userPk, List<String> roles) {
        // Access Token 생성
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();

        Date accessTokenExpiredIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder().setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(accessTokenExpiredIn) // set Expire Time
                .setId(UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘, secret값 세팅
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiredIn)
                .setId(UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 저장
        RefreshToken refreshTokenHash = RefreshToken.builder()
                .userId(BigInteger.valueOf(Integer.parseInt(userPk)))
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenHash);

        return LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredIn(accessTokenExpiredIn.getTime())
                .build();
    }

    // Jwt 토큰에서 회원 구별 정보 추출
    public String getUserPk(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                .getSubject();
    }

    // Request의 Header에서 token 파싱 : "Authorization: JWT {{token}}"
    public String resolveToken(HttpServletRequest req) {
        String authHdr = req.getHeader("Authorization");
        if (authHdr == null || !authHdr.substring(0, 7).equals("Bearer ")) {
            return null;
        }
        return authHdr.substring(7);
    }

    // Jwt 토큰으로 인증 정보를 조회
    public Authentication getAuthentication(String token) {
        String userPk = this.getUserPk(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPk);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Jwt 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.");
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean validateAccessToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            throw new ApiException(ExceptionEnum.ACCESS_TOKEN_ERROR);
        }
    }

    public boolean validateRefreshToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
        }
    }

    // Token의 유효시간 가져오기
    public Long getExpiredIn(String token) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
        // 현재 시간
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}

package co.dalicious.client.core.filter.provider;

import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// JWT 토큰을 생성 및 검증 모듈
@Slf4j
@RequiredArgsConstructor
@Component
@PropertySource("classpath:application-jwt.properties")
public class JwtTokenProvider {

  private final static long TOKEN_VALID_MILLISECONDS = 1000L * 60 * 60 * 24 * 365; // 1년만 토큰 유효

  @Builder
  @Getter
  public static class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String idToken;
    private Long expiresIn;
  }

  private Key key;

  @Value("${spring.jwt.secret}")
  private String secretKey;

  private final UserDetailsService userDetailsService;

  private long tokenValidMilisecond = 1000L * 60 * 60; // 1시간만 토큰 유효

  @PostConstruct
  protected void init() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    key = Keys.hmacShaKeyFor(keyBytes);
  }

  // Jwt 토큰 생성
  public TokenResponseDto createToken(String userPk, List<String> roles, String displayName,
      String avatarUrl) {
    Claims accessTokenClaims = Jwts.claims().setSubject(userPk);
    accessTokenClaims.put("roles", roles);
    Date now = new Date();

    String accessToken = Jwts.builder().setClaims(accessTokenClaims) // 데이터
        .setIssuedAt(now) // 토큰 발행일자
        .setExpiration(new Date(now.getTime() + TOKEN_VALID_MILLISECONDS)) // set Expire Time
        .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘, secret값 세팅
        .compact();

    Claims idTokenClaims = Jwts.claims().setSubject(userPk);
    idTokenClaims.put("displayName", displayName);
    idTokenClaims.put("avatarUrl", avatarUrl);
    String idToken = Jwts.builder().setClaims(idTokenClaims) // 데이터
        .setIssuedAt(now) // 토큰 발행일자
        .setExpiration(new Date(now.getTime() + TOKEN_VALID_MILLISECONDS)) // set Expire Time
        .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘, secret값 세팅
        .compact();

    return TokenResponseDto.builder().accessToken(accessToken).refreshToken("").idToken(idToken)
        .expiresIn(TOKEN_VALID_MILLISECONDS).build();
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
    } catch (Exception e) {
      return false;
    }
  }
}

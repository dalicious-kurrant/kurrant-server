package co.dalicious.client.core.filter;

import java.io.IOException;
import java.net.URI;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.dalicious.data.redis.entity.BlackListTokenHash;
import co.dalicious.data.redis.repository.BlackListTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final BlackListTokenRepository blackListTokenRepository;

    // Request로 들어오는 Jwt Token의 유효성을 검증(jwtTokenProvider.validateToken)하는 filter를 filterChain에 등록합니다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equals("OPTIONS")) {
            logger.debug("if request options method is options, return true");
            return;
        }

        // TODO: 도메인 적용시 변경
        URI uri = UriComponentsBuilder.fromHttpUrl(String.valueOf(request.getRequestURL())).build().toUri();
        if (uri.getPort() == 8888 || uri.getPort() == 8886 || uri.getPort() == 8884) {
            filterChain.doFilter(request, response);
            return;
        }

        // /v1/auth로 시작하는 모든 요청 필터링 건너뛰기
        if (request.getRequestURI().startsWith("/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Request Header에서 JWT 토큰 추출
        String jwtToken = null;
        try {
            jwtToken = jwtTokenProvider.resolveToken(request);
        } catch (IllegalArgumentException ex) {
            log.error("Unable to get JWT token", ex);
            throw ex;
        }


        // } catch (ExpiredJwtException ex) {
        // log.error("JWT Token has expired", ex);
        // throw new ExpiredJwtException("JWT Token has expired");
        // } catch (UsernameFromTokenException ex) {
        // log.error("token valid error:" + ex.getMessage() ,ex);
        // throw new UsernameFromTokenException("Username from token error");
        // }


        // 2. 토큰을 가져오면 유효성 검사를 한다, null인 경우는 안들어온 것

        if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {
            log.info("AccessToken >>>> " + jwtToken);
            // Redis에서 해당 accessToken logout 여부 확인
            BlackListTokenHash blackListTokenHash = blackListTokenRepository.findByAccessToken(jwtToken);
            if(blackListTokenHash == null) {
                Authentication auth = jwtTokenProvider.getAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}

package co.kurrant.app.admin_api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminJwtAuthenticationFilter extends OncePerRequestFilter {

    private final AdminJwtTokenProvider jwtTokenProvider;

    // Request로 들어오는 Jwt Token의 유효성을 검증(jwtTokenProvider.validateToken)하는 filter를 filterChain에 등록합니다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
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
        log.info("TokenValidate: " + jwtTokenProvider.validateToken(jwtToken));

        if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {
            log.info("AccessToken >>>> " + jwtToken);
            Authentication auth = jwtTokenProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}

package co.dalicious.client.core.filter;

import co.dalicious.client.core.filter.provider.SimpleJwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleJwtAuthenticationFilter extends OncePerRequestFilter {

    private final SimpleJwtTokenProvider jwtTokenProvider;

    // Request로 들어오는 Jwt Token의 유효성을 검증(jwtTokenProvider.validateToken)하는 filter를 filterChain에 등록합니다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equals("OPTIONS")) {
            logger.debug("if request options method is options, return true");
            return;
        }

        URI prodUri = UriComponentsBuilder.fromHttpUrl(String.valueOf(request.getRequestURL())).build().toUri();
        if(prodUri.getHost().contains("prod.dalicious.co") || prodUri.getHost().contains("3.39.196.44")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().startsWith("/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        List<String> excludedUrl = new ArrayList<>();
        excludedUrl.add("/v1/auth/login");
        excludedUrl.add("/v1/auth/login");
        excludedUrl.add("/v1/makers/login");
        // url에 login이 포함 되어 욌으면 토큰 검증을 하지 않음.
        URI uri = UriComponentsBuilder.fromHttpUrl(String.valueOf(request.getRequestURL())).build().toUri();
        if(excludedUrl.contains(uri.getPath())) {
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
        log.info("TokenValidate: " + jwtTokenProvider.validateToken(jwtToken));

        if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {
            log.info("AccessToken >>>> " + jwtToken);
            Authentication auth = jwtTokenProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}

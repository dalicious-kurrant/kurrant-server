package co.dalicious.client.core.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
    super.setAuthenticationManager(authenticationManager);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    CustomAuthenticationFilter.log.info("Trying to authentication...");

    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
        request.getParameter("email"), request.getParameter("password"));
    setDetails(request, authRequest);
    return this.getAuthenticationManager().authenticate(authRequest);
  }
}

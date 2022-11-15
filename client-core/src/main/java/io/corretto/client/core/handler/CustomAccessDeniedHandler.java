package io.corretto.client.core.handler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.corretto.client.core.exception.ApiException;
import io.corretto.client.core.exception.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException exception) throws IOException {
    CustomAccessDeniedHandler.log.warn("Access denied!");

    ObjectMapper mapper = new ObjectMapper();

    ApiException ex = new ApiException(ExceptionEnum.UNAUTHORIZED);
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    out.print(mapper.writeValueAsString(ex));
    out.flush();
  }
}

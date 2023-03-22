package co.dalicious.client.core.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException, ServletException {
        CustomAuthenticationHandler.log.warn("Token errored.");
        String requestId = (String) request.getAttribute("requestId");
//    throw new ApiException(ExceptionEnum.ACCESS_TOKEN_ERROR);
        this.sendError(requestId, HttpStatus.FORBIDDEN, response, ex);
    }

    private void sendError(String requestId, HttpStatus status, HttpServletResponse response,
                           Throwable ex) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("id", requestId);

        Map<String, String> errorItem = new HashMap<>();
        errorItem.put("code", "401");
        errorItem.put("message", "접근 권한이 없습니다.");
        List<Map<String, String>> errorItems = new ArrayList<>();
        errorItems.add(errorItem);
        errorDetails.put("errors", errorItems);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), errorDetails);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

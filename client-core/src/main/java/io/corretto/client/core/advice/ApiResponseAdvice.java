package io.corretto.client.core.advice;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import io.corretto.client.core.dto.response.ErrorItemResponseDto;
import io.corretto.client.core.dto.response.ResponseDto;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

  @Autowired
  private HttpServletRequest httpServletRequest;

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {

    // 확실히 SWAGGER로 시작하는 애들만 해준다.
    String uri = httpServletRequest.getRequestURI();
    if (uri.startsWith("/swagger-resources") || uri.startsWith("/swagger-ui")
        || uri.equals("/api-docs")) {
      return body;
    }

    ResponseDto.ResponseDtoBuilder<Object> builder =
        ResponseDto.builder().id((String) httpServletRequest.getAttribute("requestId"));

    if (body instanceof List && ((List<?>) body).get(0) instanceof ErrorItemResponseDto) {
      builder.errors((List<ErrorItemResponseDto>) body);
    } else {
      builder.data(body);
    }

    return builder.build();
  }
}

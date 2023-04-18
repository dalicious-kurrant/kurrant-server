package co.dalicious.client.core.advice;

import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import co.dalicious.client.core.dto.response.ErrorItemResponseDto;
import co.dalicious.client.core.dto.response.ResponseDto;
import co.dalicious.client.core.dto.response.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

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

        if (body instanceof List && !((List<?>) body).isEmpty() && ((List<?>) body).get(0) != null && ((List<?>) body).get(0).getClass().equals(ErrorItemResponseDto.class)) {
            ErrorItemResponseDto errorItemResponseDto = ((List<ErrorItemResponseDto>) body).get(0);

            builder.error(errorItemResponseDto.getCode());
            builder.message(errorItemResponseDto.getMessage());
            builder.statusCode((errorItemResponseDto.getCode().contains("CE")) ? Integer.parseInt(errorItemResponseDto.getCode().substring(2, 5)) : Integer.parseInt(errorItemResponseDto.getCode().substring(1, 4)));
        } else if (body.getClass().equals(LinkedHashMap.class)) {
            builder.error("SE" + ((LinkedHashMap<?, ?>) body).get("status"));
            builder.message((String) ((LinkedHashMap<?, ?>) body).get("error"));
            builder.statusCode((int) ((LinkedHashMap<?, ?>) body).get("status"));
        } else if (body.getClass().equals(ResponseMessage.class)) {
            builder.data(((ResponseMessage) body).getData());
            builder.message(((ResponseMessage) body).getMessage());
            builder.statusCode(200);
        } else {
            builder.data(body);
            builder.statusCode(200);
        }
        return builder.build();
    }
}

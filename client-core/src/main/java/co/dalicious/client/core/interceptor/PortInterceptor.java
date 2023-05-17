package co.dalicious.client.core.interceptor;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.interceptor.holder.RequestContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class PortInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int port = request.getServerPort();
        String endpoint = request.getRequestURI();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + port;

        RequestContextHolder.setCurrentPort(port);
        RequestContextHolder.setCurrentEndpoint(endpoint);
        RequestContextHolder.setCurrentBaseUrl(baseUrl);

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();

            ControllerMarker controllerMarker = method.getAnnotation(ControllerMarker.class);
            if (controllerMarker != null) {
                RequestContextHolder.setCurrentControllerType(controllerMarker.value());
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestContextHolder.clear();
    }
}

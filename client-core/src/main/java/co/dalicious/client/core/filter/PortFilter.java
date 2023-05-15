package co.dalicious.client.core.filter;

import co.dalicious.client.core.filter.provider.RequestContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class PortFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int port = request.getServerPort();
        String endpoint = request.getRequestURI();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + port;

        RequestContextHolder.setCurrentPort(port);
        RequestContextHolder.setCurrentEndpoint(endpoint);
        RequestContextHolder.setCurrentBaseUrl(baseUrl);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestContextHolder.clear();
    }
}

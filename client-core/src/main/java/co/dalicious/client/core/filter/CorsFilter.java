package co.dalicious.client.core.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");

        if (origin == null){
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        } else if(origin.startsWith("http://localhost:3000") || origin.startsWith("http://localhost:3001") || origin.startsWith("http://localhost:3002")
                || origin.startsWith("http://localhost:3003") || origin.startsWith("http://localhost:3004") || origin.startsWith("http://localhost:3005")
                || origin.startsWith("http://localhost:3006") || origin.startsWith("http://localhost:3007 ")|| origin.startsWith("http://localhost:3008")
                || origin.startsWith("http://localhost:3009") || origin.startsWith("http://localhost:3010")||origin.startsWith("https://kurrant.co")
                || origin.startsWith("http://3.35.197.186") || origin.startsWith("http://3.39.196.44")
                || origin.startsWith("http://admin.dalicious.co") || origin.startsWith("http://group.dalicious.co")|| origin.startsWith("http://makers.dalicious.co")
                || origin.startsWith("https://admin.dalicious.co") || origin.startsWith("https://group.dalicious.co")|| origin.startsWith("https://makers.dalicious.co")
                || origin.startsWith("https://prod.dalicious.co") || origin.startsWith("https://dev.dalicious.co")|| origin.startsWith("https://dalicious.co")
                || origin.startsWith("https://kurrant.shop") || origin.startsWith("https://dev.server.kurrant.shop") || origin.startsWith("https://admin.server.kurrant.shop")
                || origin.startsWith("https://makers.server.kurrant.shop") || origin.startsWith("https://group.server.kurrant.shop")
                || origin.startsWith("https://api.tosspayments.com/v1/payments/")){
            response.setHeader("Access-Control-Allow-Origin", origin); //허용대상 도메인
        }
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, PATCH, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");

        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");


        chain.doFilter(req, res);
    }
    public void init(FilterConfig filterConfig) {}
    public void destroy() {}
}

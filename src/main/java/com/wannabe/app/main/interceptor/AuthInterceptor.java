package com.wannabe.app.main.interceptor;

import com.wannabe.app.main.service.AuthInterceptorService;
import com.wannabe.app.main.utility.LogUtils;
import com.wannabe.app.main.utility.constant.HeaderKey;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements AsyncHandlerInterceptor {

    private final AuthInterceptorService authInterceptorService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("interceptor#preHandle called. Thread: " + Thread.currentThread().getName());

        if (!DispatcherType.REQUEST.equals(request.getDispatcherType())) {
            return true;
        }
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        if (isAdminCallable((HandlerMethod) handler)) {
            authInterceptorService.verifyAdminToken(request);
            return true;
        }

        if (isAnonymousCallable((HandlerMethod) handler)) {
            String tokenString = request.getHeader(HeaderKey.AUTHORIZATION);
            if(tokenString != null && !tokenString.isEmpty()) {
                authInterceptorService.verifyToken(request);
            }
            return true;
        }

        if (isRefreshCallable((HandlerMethod) handler)) {
            authInterceptorService.verifyRefreshToken(request);
            return true;
        }

        authInterceptorService.verifyToken(request);
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {
        if (handler instanceof HandlerMethod) {
            LogUtils.accessLog(LogManager.getLogger(((HandlerMethod) handler).getBeanType().getName()), request);
        }
    }

    private boolean isAnonymousCallable(HandlerMethod handlerMethod) {
        return authInterceptorService.isAnonymousCallable(handlerMethod);
    }

    private boolean isAdminCallable(HandlerMethod handlerMethod) {
        return authInterceptorService.isAdminCallable(handlerMethod);
    }

    private boolean isRefreshCallable(HandlerMethod handlerMethod) {
        return authInterceptorService.isRefreshCallable(handlerMethod);
    }
}

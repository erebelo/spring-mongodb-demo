package com.erebelo.springmongodbdemo.context.interceptor;

import static com.erebelo.springmongodbdemo.constant.ProfileConstant.LOGGED_IN_USER_ID_HEADER;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_401_000;

import com.erebelo.spring.common.utils.serialization.ObjectMapperProvider;
import com.erebelo.springmongodbdemo.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
@Component
@RequiredArgsConstructor
public class HeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        if (handler instanceof HandlerMethod) {
            HeaderLoggedInUser annotation = ((HandlerMethod) handler).getMethodAnnotation(HeaderLoggedInUser.class);
            if (annotation == null) {
                annotation = ((HandlerMethod) handler).getMethod().getDeclaringClass()
                        .getAnnotation(HeaderLoggedInUser.class);
            }
            if (annotation != null) {
                log.info("Checking whether request contains the loggedInUser http headers");
                String loggedInUser = request.getHeader(LOGGED_IN_USER_ID_HEADER);
                if (loggedInUser != null && !loggedInUser.trim().isEmpty()) {
                    return true;
                }
                try {
                    ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.valueOf(401),
                            COMMON_ERROR_401_000.toString().replace('_', '-'),
                            String.format("Missing %s attribute in HttpHeaders", LOGGED_IN_USER_ID_HEADER),
                            System.currentTimeMillis(), null);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(ObjectMapperProvider.INSTANCE.writeValueAsString(exceptionResponse));
                } catch (IOException e) {
                    throw new IllegalStateException(
                            "Interceptor handling failure: error writing into HttpServletResponse", e);
                }
                return false;
            }
        }
        return true;
    }
}

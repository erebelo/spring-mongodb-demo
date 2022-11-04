package com.erebelo.springmongodbdemo.context.interceptor;

import com.erebelo.springmongodbdemo.exception.ExceptionResponse;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.erebelo.springmongodbdemo.constants.ProfileConstants.LOGGED_IN_USER_ID_HEADER;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_401_000;

@Component
public class HeaderInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HeaderLoggedInUser annotation = ((HandlerMethod) handler).getMethodAnnotation(HeaderLoggedInUser.class);
            if (annotation == null) {
                annotation = ((HandlerMethod) handler).getMethod().getDeclaringClass().getAnnotation(HeaderLoggedInUser.class);
            }
            if (annotation != null) {
                LOGGER.info("Checking whether request contains the loggedInUser http headers");
                String loggedInUser = request.getHeader(LOGGED_IN_USER_ID_HEADER);
                if (loggedInUser != null && loggedInUser.trim().length() > 0) {
                    return true;
                }
                try {
                    ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.valueOf(401),
                            COMMON_ERROR_401_000.toString().replace('_', '-'),
                            String.format("Missing %s attribute in HttpHeaders", LOGGED_IN_USER_ID_HEADER), System.currentTimeMillis());
                    response.setStatus(401);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(new Gson().toJson(exceptionResponse));
                } catch (IOException e) {
                    throw new IllegalStateException("Interceptor handling failure: error writing into HttpServletResponse", e);
                }
                return false;
            }
        }
        return true;
    }
}

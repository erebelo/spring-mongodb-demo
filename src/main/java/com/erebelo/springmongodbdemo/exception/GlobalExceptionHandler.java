package com.erebelo.springmongodbdemo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.MissingFormatArgumentException;
import java.util.Objects;

@ControllerAdvice
@PropertySources({@PropertySource("classpath:common_error_messages.properties")})
public class GlobalExceptionHandler {

    @Autowired
    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(StandardException.class)
    public ExceptionResponse standardError(StandardException exception, HttpServletResponse response) {
        LOGGER.error("StandardException thrown", exception);
        return parseExceptionMessage(exception, response);

    }

    private ExceptionResponse parseExceptionMessage(StandardException exception, HttpServletResponse response) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(500, "", System.currentTimeMillis());
        response.setStatus(500);

        String propertyKey = exception.getErrorCode().propertyKey();
        if (Objects.isNull(propertyKey)) {
            String errorMsg = "Basic error handling failure: null errorCode";
            exceptionResponse.setMessage(errorMsg);
            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        String properties = env.getProperty(propertyKey);
        if (Objects.isNull(properties)) {
            String errorMsg = String.format("Basic error handling failure: no properties found for %s", propertyKey);
            exceptionResponse.setMessage(errorMsg);
            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        String[] formatArray = properties.split("\\|");
        if (formatArray.length < 2) {
            String errorMsg = String.format("Basic error handling failure: badly formatted message %s", properties);
            exceptionResponse.setMessage(errorMsg);
            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        try {
            exceptionResponse.setMessage(String.format(formatArray[1], exception.getArgs()));
        } catch (MissingFormatArgumentException e) {
            String errorMsg = String.format("Basic error handling failure: configured error message format '%s' only " +
                    "provided %s values for injection", properties, exception.getArgs().length);
            exceptionResponse.setMessage(errorMsg);
            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        try {
            int httpStatusCode = Integer.parseInt(formatArray[0]);
            response.setStatus(httpStatusCode);
            exceptionResponse.setStatus(httpStatusCode);
        } catch (NumberFormatException e) {
            String errorMsg = String.format("Basic error handling failure: could not get http status code from %s",
                    properties);
            exceptionResponse.setMessage(errorMsg);
            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        LOGGER.info("The message error is: {}", exceptionResponse.getMessage());
        return exceptionResponse;
    }
}

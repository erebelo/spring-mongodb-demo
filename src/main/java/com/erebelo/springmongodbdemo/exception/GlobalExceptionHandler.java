package com.erebelo.springmongodbdemo.exception;

import com.erebelo.springmongodbdemo.exception.model.ApplicationException;
import com.erebelo.springmongodbdemo.exception.model.StandardException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_400_000;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_000;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_500_000;

@ControllerAdvice
@RequiredArgsConstructor
@PropertySources(@PropertySource("classpath:common_error_messages.properties"))
public class GlobalExceptionHandler {

    private final Environment env;
    private final ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ExceptionResponse handleException(Exception exception, HttpServletResponse response) {
        LOGGER.error("Exception thrown:", exception);
        return parseExceptionMessage(500, COMMON_ERROR_500_000.toString(), exception.getMessage(), response);
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    public ExceptionResponse handleIllegalStateException(IllegalStateException exception, HttpServletResponse response) {
        LOGGER.error("IllegalStateException thrown:", exception);
        return parseExceptionMessage(500, COMMON_ERROR_500_000.toString(), exception.getMessage(), response);
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ExceptionResponse handleIllegalArgumentException(IllegalArgumentException exception, HttpServletResponse response) {
        LOGGER.error("IllegalArgumentException thrown:", exception);
        return parseExceptionMessage(400, COMMON_ERROR_400_000.toString(), exception.getMessage(), response);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ExceptionResponse handleConstraintViolationException(ConstraintViolationException exception, HttpServletResponse response) {
        LOGGER.error("ConstraintViolationException thrown:", exception);
        return parseExceptionMessage(422, COMMON_ERROR_422_000.toString(), exception.getMessage(), response);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ExceptionResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, HttpServletResponse response) {
        LOGGER.error("HttpMessageNotReadableException thrown:", exception);
        return parseExceptionMessage(422, COMMON_ERROR_422_000.toString(), exception.getMessage(), response);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletResponse response) {
        LOGGER.error("MethodArgumentNotValidException thrown:", exception);

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String message = "No message found";
        if (!fieldErrors.isEmpty()) {
            message = fieldErrors.stream()
                    .map(FieldError::getDefaultMessage)
                    .toList()
                    .toString();
        }

        return parseExceptionMessage(422, COMMON_ERROR_422_000.toString(), message, response);
    }

    @ResponseBody
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionResponse> handleApplicationException(ApplicationException exception) {
        LOGGER.error("ApplicationException thrown:", exception);
        return parseApplicationExceptionMessage(exception.getHttpStatus(), exception.getMessage(), exception.getCause());
    }

    @ResponseBody
    @ExceptionHandler(StandardException.class)
    public ExceptionResponse handleStandardException(StandardException exception, HttpServletResponse response) {
        LOGGER.error("StandardException thrown:", exception);
        return parseStandardExceptionMessage(exception, response);
    }

    private ExceptionResponse parseExceptionMessage(final int httpStatusCode, final String code, final String message, HttpServletResponse response) {
        response.setStatus(httpStatusCode);
        return new ExceptionResponse(HttpStatus.valueOf(httpStatusCode), code.replace('_', '-'), message, System.currentTimeMillis(), null);
    }

    private ResponseEntity<ExceptionResponse> parseApplicationExceptionMessage(final HttpStatus httpStatus, final String message,
            final Throwable cause) {
        var errorHttpStatus = ObjectUtils.isEmpty(httpStatus) ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus;
        var errorMessage = ObjectUtils.isEmpty(message) ? "No defined message" : message;

        Object clientErrorObj = null;
        if (cause != null) {
            var clientErrorMessage = ObjectUtils.isEmpty(cause.getMessage()) ? null : cause.getMessage();

            if (clientErrorMessage != null) {
                clientErrorObj = extractJsonObject(clientErrorMessage);
            }
        }

        return ResponseEntity.status(httpStatus).body(new ExceptionResponse(errorHttpStatus, null, errorMessage, System.currentTimeMillis(),
                clientErrorObj));
    }

    ExceptionResponse parseStandardExceptionMessage(final StandardException exception, HttpServletResponse response) {
        response.setStatus(500);
        var exceptionResponse = new ExceptionResponse(HttpStatus.valueOf(500), COMMON_ERROR_500_000.toString(), "", System.currentTimeMillis(), null);

        var propertyKey = exception.getErrorCode().propertyKey();
        if (Objects.isNull(propertyKey)) {
            var errorMsg = "Basic error handling failure: null errorCode";
            exceptionResponse.setMessage(errorMsg);

            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        var properties = env.getProperty(propertyKey);
        if (Objects.isNull(properties)) {
            var errorMsg = String.format("Basic error handling failure: no properties found for %s", propertyKey);
            exceptionResponse.setMessage(errorMsg);

            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        var formatArray = properties.split("\\|");
        if (formatArray.length < 2) {
            var errorMsg = String.format("Basic error handling failure: badly formatted message %s", properties);
            exceptionResponse.setMessage(errorMsg);

            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        try {
            int httpStatusCode = Integer.parseInt(formatArray[0]);
            response.setStatus(httpStatusCode);
            exceptionResponse.setStatus(HttpStatus.valueOf(httpStatusCode));
            exceptionResponse.setCode(propertyKey);
            exceptionResponse.setMessage(String.format(formatArray[1], exception.getArgs()));
        } catch (NumberFormatException e) {
            var errorMsg = String.format("Basic error handling failure: could not get http status code from %s", properties);
            exceptionResponse.setMessage(errorMsg);

            LOGGER.error(errorMsg);
            return exceptionResponse;
        }

        LOGGER.info("The message error is: {}", exceptionResponse.getMessage());
        return exceptionResponse;
    }

    private Object extractJsonObject(String clientErrorMessage) {
        try {
            int startIndex = clientErrorMessage.indexOf('{');
            int endIndex = clientErrorMessage.lastIndexOf('}') + 1;
            String jsonString = clientErrorMessage.substring(startIndex, endIndex);

            return objectMapper.readValue(jsonString, Object.class);
        } catch (Exception e) {
            LOGGER.warn("Error parsing JSON string to JSON object", e);
            return null;
        }
    }
}

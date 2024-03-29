package com.erebelo.springmongodbdemo.exception;

import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
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
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        LOGGER.error("Exception thrown:", exception);
        return parseExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalStateException(IllegalStateException exception) {
        LOGGER.error("IllegalStateException thrown:", exception);
        return parseExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        LOGGER.error("IllegalArgumentException thrown:", exception);
        return parseExceptionMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        LOGGER.error("ConstraintViolationException thrown:", exception);

        String errorMessage = null;
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        if (!ObjectUtils.isEmpty(violations)) {
            List<String> messages = violations.stream().map(ConstraintViolation::getMessage).toList();
            errorMessage = String.join(";", messages);
        }

        return parseExceptionMessage(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        LOGGER.error("HttpMessageNotReadableException thrown:", exception);
        return parseExceptionMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        LOGGER.error("HttpRequestMethodNotSupportedException thrown:", exception);

        String errorMessage = exception.getMessage();
        var supportedHttpMethods = exception.getSupportedMethods();
        if (!ObjectUtils.isEmpty(supportedHttpMethods)) {
            errorMessage += ". Supported methods: " + String.join(", ", supportedHttpMethods);
        }

        return parseExceptionMessage(HttpStatus.METHOD_NOT_ALLOWED, errorMessage);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        LOGGER.error("MethodArgumentNotValidException thrown:", exception);

        String errorMessage = null;
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            errorMessage = fieldErrors.stream()
                    .map(FieldError::getDefaultMessage)
                    .toList()
                    .toString();
        }

        return parseExceptionMessage(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ExceptionResponse> handleTransactionSystemException(TransactionSystemException exception) {
        LOGGER.error("TransactionSystemException thrown:", exception);

        var errorMessage = "An error occurred during transaction processing";
        var rootCause = exception.getRootCause();
        if (rootCause != null && !ObjectUtils.isEmpty(rootCause.getMessage())) {
            errorMessage += ". Root cause: " + rootCause.getMessage();
        }

        return parseExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    @ResponseBody
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ExceptionResponse> handleClientException(ClientException exception) {
        LOGGER.error("ClientException thrown:", exception);
        return parseClientExceptionMessage(exception.getHttpStatus(), exception.getMessage(), exception.getCause());
    }

    @ResponseBody
    @ExceptionHandler(CommonException.class)
    public ExceptionResponse handleCommonException(CommonException exception, HttpServletResponse response) {
        LOGGER.error("CommonException thrown:", exception);
        return parseCommonExceptionMessage(exception, response);
    }

    private ResponseEntity<ExceptionResponse> parseExceptionMessage(final HttpStatus httpStatus, final String message) {
        var errorHttpStatus = ObjectUtils.isEmpty(httpStatus) ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus;
        var errorMessage = ObjectUtils.isEmpty(message) ? "No defined message" : message;

        return ResponseEntity.status(httpStatus).body(new ExceptionResponse(errorHttpStatus, null, errorMessage, System.currentTimeMillis(), null));
    }

    private ResponseEntity<ExceptionResponse> parseClientExceptionMessage(final HttpStatus httpStatus, final String message, final Throwable cause) {
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

    ExceptionResponse parseCommonExceptionMessage(final CommonException exception, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        var exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, COMMON_ERROR_500_000.toString(), "",
                System.currentTimeMillis(), null);

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

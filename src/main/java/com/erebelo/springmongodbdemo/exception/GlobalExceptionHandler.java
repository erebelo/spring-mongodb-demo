package com.erebelo.springmongodbdemo.exception;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.GLOBAL_EXCEPTION_MESSAGE;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.LINE_DELIMITERS;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.REQUEST_ID_HEADER;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_500_000;

import com.erebelo.spring.common.utils.serialization.ObjectMapperProvider;
import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2
@ControllerAdvice
@RequiredArgsConstructor
@PropertySources(@PropertySource("classpath:common-error-messages.properties"))
public class GlobalExceptionHandler {

    private final Environment env;

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        return parseGeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalStateException(IllegalStateException e) {
        return parseGeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return parseGeneralException(HttpStatus.BAD_REQUEST, e);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return parseGeneralException(HttpStatus.BAD_REQUEST, e);
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e) {
        return parseGeneralException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return parseGeneralException(HttpStatus.BAD_REQUEST, e);
    }

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        String errorMessage = e.getMessage();
        var supportedHttpMethods = e.getSupportedMethods();
        if (!ObjectUtils.isEmpty(supportedHttpMethods)) {
            errorMessage += ". Supported methods: " + String.join(", ", supportedHttpMethods);
        }

        return parseGeneralException(HttpStatus.METHOD_NOT_ALLOWED, e, errorMessage);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = null;
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            errorMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).toList().toString();
        }

        return parseGeneralException(HttpStatus.BAD_REQUEST, e, errorMessage);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ExceptionResponse> handleTransactionSystemException(TransactionSystemException e) {
        var errorMessage = "An error occurred during transaction processing";
        var rootCause = e.getRootCause();
        if (rootCause != null && !ObjectUtils.isEmpty(rootCause.getMessage())) {
            errorMessage += ". Root cause: " + rootCause.getMessage();
        }

        return parseGeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e, errorMessage);
    }

    @ResponseBody
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ExceptionResponse> handleClientException(ClientException e) {
        return parseClientException(e.getHttpStatus(), e.getMessage(), e.getCause());
    }

    @ResponseBody
    @ExceptionHandler(CommonException.class)
    public ExceptionResponse handleCommonException(CommonException e, HttpServletResponse response) {
        return parseCommonException(e, response);
    }

    private ResponseEntity<ExceptionResponse> parseGeneralException(final HttpStatus httpStatus, final Exception e) {
        return parseGeneralException(httpStatus, e, e.getMessage());
    }

    private ResponseEntity<ExceptionResponse> parseGeneralException(final HttpStatus httpStatus, final Exception e,
            final String message) {
        var errorHttpStatus = ObjectUtils.isEmpty(httpStatus) ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus;
        var errorMessage = ObjectUtils.isEmpty(message) ? "No defined message" : message;
        var exceptionResponse = new ExceptionResponse(errorHttpStatus, null, errorMessage, System.currentTimeMillis(),
                null);

        log.error(GLOBAL_EXCEPTION_MESSAGE + " {}", ThreadContext.get(REQUEST_ID_HEADER), exceptionResponse,
                getStackTrace(e));
        return ResponseEntity.status(httpStatus).body(exceptionResponse);
    }

    private ResponseEntity<ExceptionResponse> parseClientException(final HttpStatus httpStatus, final String message,
            final Throwable cause) {
        var errorHttpStatus = ObjectUtils.isEmpty(httpStatus) ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus;
        var errorMessage = ObjectUtils.isEmpty(message) ? "No defined message" : message;

        Object clientErrorObj = null;
        if (cause != null) {
            var clientErrorMessage = ObjectUtils.isEmpty(cause.getMessage()) ? null : cause.getMessage();

            if (!ObjectUtils.isEmpty(clientErrorMessage)) {
                clientErrorObj = extractJsonObject(clientErrorMessage);
            }
        }

        var exceptionResponse = new ExceptionResponse(errorHttpStatus, null, errorMessage, System.currentTimeMillis(),
                clientErrorObj);

        exceptionResponseLogError(exceptionResponse);
        return ResponseEntity.status(httpStatus).body(exceptionResponse);
    }

    ExceptionResponse parseCommonException(final CommonException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        var exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, COMMON_ERROR_500_000.toString(),
                "", System.currentTimeMillis(), null);

        var propertyKey = e.getErrorCode().propertyKey();
        if (Objects.isNull(propertyKey)) {
            exceptionResponse.setMessage("Basic error handling failure: null errorCode");
            exceptionResponseLogError(exceptionResponse);
            return exceptionResponse;
        }

        var properties = env.getProperty(propertyKey);
        if (Objects.isNull(properties)) {
            exceptionResponse
                    .setMessage(String.format("Basic error handling failure: no properties found for %s", propertyKey));
            exceptionResponseLogError(exceptionResponse);
            return exceptionResponse;
        }

        var formatArray = properties.split("\\|");
        if (formatArray.length < 2) {
            exceptionResponse
                    .setMessage(String.format("Basic error handling failure: badly formatted message %s", properties));
            exceptionResponseLogError(exceptionResponse);
            return exceptionResponse;
        }

        try {
            int httpStatusCode = Integer.parseInt(formatArray[0]);
            response.setStatus(httpStatusCode);
            exceptionResponse.setStatus(HttpStatus.valueOf(httpStatusCode));
            exceptionResponse.setCode(propertyKey);
            exceptionResponse.setMessage(String.format(formatArray[1], e.getArgs()));
        } catch (NumberFormatException numberFormatException) {
            exceptionResponse.setMessage(
                    String.format("Basic error handling failure: could not get http status code from %s", properties));
            exceptionResponseLogError(exceptionResponse);
            return exceptionResponse;
        }

        exceptionResponseLogError(exceptionResponse);
        return exceptionResponse;
    }

    private Object extractJsonObject(String clientErrorMessage) {
        try {
            int startIndex = clientErrorMessage.indexOf('{');
            int endIndex = clientErrorMessage.lastIndexOf('}') + 1;
            String jsonString = clientErrorMessage.substring(startIndex, endIndex);

            return ObjectMapperProvider.INSTANCE.readValue(jsonString, Object.class);
        } catch (Exception e) {
            log.warn("Error parsing JSON string to JSON object", e);
            return null;
        }
    }

    private String getStackTrace(Exception e) {
        if (env.acceptsProfiles(Profiles.of("local"))) {
            return ExceptionUtils.getStackTrace(e);
        }

        StringBuilder stackTrace = new StringBuilder();
        List<Throwable> causeChain = ExceptionUtils.getThrowableList(e);

        for (Throwable cause : causeChain) {
            String[] stackTraceElements = ExceptionUtils.getStackFrames(cause);
            stackTrace.append("Caused by: ");

            for (int i = 0; i < Math.min(4, stackTraceElements.length); i++) {
                stackTrace.append(stackTraceElements[i]).append(" ");
            }
        }

        return stackTrace.toString().replaceAll(LINE_DELIMITERS, "").trim();
    }

    private void exceptionResponseLogError(ExceptionResponse exceptionResponse) {
        log.error(GLOBAL_EXCEPTION_MESSAGE, ThreadContext.get(REQUEST_ID_HEADER), exceptionResponse);
    }
}

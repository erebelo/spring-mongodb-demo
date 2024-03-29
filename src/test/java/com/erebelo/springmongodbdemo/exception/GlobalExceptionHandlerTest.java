package com.erebelo.springmongodbdemo.exception;

import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.exception.model.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_400_000;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_500_000;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@ExtendWith(SpringExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Environment env;

    private static final ErrorCode ERROR_CODE_NO_CODE = () -> "COMMON_900_NOCODE";
    private static final ErrorCode ERROR_CODE_NO_DESC = () -> "COMMON_900_NODESC";
    private static final ErrorCode ERROR_CODE_NO_KEY = () -> null;

    @BeforeEach
    public void init() {
        response = spy(new MockHttpServletResponse());
    }

    @Test
    void testException() {
        var responseEntity = handler.handleException(new Exception("Exception error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        var exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("Exception error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testIllegalStateException() {
        var responseEntity = handler.handleIllegalStateException(new IllegalStateException("IllegalStateException error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        var exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("IllegalStateException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testIllegalArgumentException() {
        var responseEntity = handler.handleIllegalArgumentException(new IllegalArgumentException("IllegalArgumentException error"));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        var exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("IllegalArgumentException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        given(violation.getMessage()).willReturn("ConstraintViolationException error");

        var exceptionMock = mock(ConstraintViolationException.class);
        given(exceptionMock.getConstraintViolations()).willReturn(Set.of(violation));

        var responseEntity = handler.handleConstraintViolationException(exceptionMock);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        var exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("ConstraintViolationException error", exceptionResponse.getMessage());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testHttpMessageNotReadableException() {
        var responseEntity = handler.handleHttpMessageNotReadableException(new HttpMessageNotReadableException(
                "HttpMessageNotReadableException error", mock(HttpInputMessage.class)));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        var exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("HttpMessageNotReadableException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testHttpRequestMethodNotSupportedException() {
        List<String> supportedMethods = new ArrayList<>();
        supportedMethods.add("GET");
        supportedMethods.add("POST");

        var responseEntity = handler.handleHttpRequestMethodNotSupportedException(new HttpRequestMethodNotSupportedException(
                "HttpRequestMethodNotSupportedException error", supportedMethods));
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

        var exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("Request method 'HttpRequestMethodNotSupportedException error' is not supported. Supported methods: GET, POST",
                exceptionResponse.getMessage());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testMethodArgumentNotValidException() {
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("objectName", "fieldName1", "MethodArgumentNotValidException error 1"));
        fieldErrors.add(new FieldError("objectName", "fieldName2", "MethodArgumentNotValidException error 2"));

        var bindingResultMock = mock(BindingResult.class);
        given(bindingResultMock.getFieldErrors()).willReturn(fieldErrors);

        var exceptionMock = mock(MethodArgumentNotValidException.class);
        given(exceptionMock.getBindingResult()).willReturn(bindingResultMock);

        var responseEntity = handler.handleMethodArgumentNotValidException(exceptionMock);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        var exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("[MethodArgumentNotValidException error 1, MethodArgumentNotValidException error 2]", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testTransactionSystemException() {
        var responseEntity = handler.handleTransactionSystemException(new TransactionSystemException("TransactionSystemException error",
                new RuntimeException("TransactionSystemException error")));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        var exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("An error occurred during transaction processing. Root cause: TransactionSystemException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testCommonException() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|%s");

        var exceptionResponse = handler.handleCommonException(new CommonException(COMMON_ERROR_400_000, new Exception("Exception error"),
                "CommonException error"), response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("CommonException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testCommonExceptionWithNoArgs() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|%s");

        var exceptionResponse = handler.handleCommonException(new CommonException(COMMON_ERROR_400_000, new Exception("Exception error")),
                response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("null", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseCommonExceptionSuccessfully() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|Bad Request to user %s");

        var exceptionResponse = handler.parseCommonExceptionMessage(new CommonException(COMMON_ERROR_400_000, "foo"), response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("Bad Request to user foo", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseCommonExceptionMessageNullErrorCode() {
        var exceptionResponse = handler.parseCommonExceptionMessage(new CommonException(ERROR_CODE_NO_KEY), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: null errorCode", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseCommonExceptionMessageMissingProperties() {
        var exceptionResponse = handler.parseCommonExceptionMessage(new CommonException(COMMON_ERROR_500_000), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: no properties found for COMMON-ERROR-500-000", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseCommonExceptionBadMessageProperties() {
        given(env.getProperty(ERROR_CODE_NO_DESC.propertyKey())).willReturn("900|");

        var exceptionResponse = handler.parseCommonExceptionMessage(new CommonException(ERROR_CODE_NO_DESC), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: badly formatted message 900|", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseCommonExceptionBadStatusCodeProperties() {
        given(env.getProperty(ERROR_CODE_NO_CODE.propertyKey())).willReturn("|Other stuff");

        var exceptionResponse = handler.parseCommonExceptionMessage(new CommonException(ERROR_CODE_NO_CODE), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: could not get http status code from |Other stuff", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }
}

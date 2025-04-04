package com.erebelo.springmongodbdemo.exception;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_400_000;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_500_000;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.exception.model.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
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
    void testExceptionWithLocalProfile() {
        given(env.acceptsProfiles(Profiles.of("local"))).willReturn(true);

        ResponseEntity<ExceptionResponse> responseEntity = handler.handleException(new Exception("Exception error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("Exception error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testIllegalStateException() {
        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleIllegalStateException(new IllegalStateException("IllegalStateException error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("IllegalStateException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testIllegalArgumentException() {
        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleIllegalArgumentException(new IllegalArgumentException("IllegalArgumentException error"));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("IllegalArgumentException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testConstraintViolationException() {
        ConstraintViolationException exceptionMock = mock(ConstraintViolationException.class);
        given(exceptionMock.getMessage()).willReturn("property: ConstraintViolationException error");

        ResponseEntity<ExceptionResponse> responseEntity = handler.handleConstraintViolationException(exceptionMock);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("property: ConstraintViolationException error", exceptionResponse.getMessage());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void tesHttpMediaTypeNotSupportedException() {
        ResponseEntity<ExceptionResponse> responseEntity = handler.handleHttpMediaTypeNotSupportedException(
                new HttpMediaTypeNotSupportedException("HttpMediaTypeNotSupportedException error"));
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("HttpMediaTypeNotSupportedException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testHttpMessageNotReadableException() {
        ResponseEntity<ExceptionResponse> responseEntity = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("HttpMessageNotReadableException error",
                        mock(HttpInputMessage.class)));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
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

        ResponseEntity<ExceptionResponse> responseEntity = handler.handleHttpRequestMethodNotSupportedException(
                new HttpRequestMethodNotSupportedException("HttpRequestMethodNotSupportedException error",
                        supportedMethods));
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals(
                "Request method 'HttpRequestMethodNotSupportedException error' is not supported. Supported methods: "
                        + "GET, POST",
                exceptionResponse.getMessage());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testMethodArgumentNotValidException() {
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("objectName", "fieldName1", "MethodArgumentNotValidException error 1"));
        fieldErrors.add(new FieldError("objectName", "fieldName2", "MethodArgumentNotValidException error 2"));

        BindingResult bindingResultMock = mock(BindingResult.class);
        given(bindingResultMock.getFieldErrors()).willReturn(fieldErrors);

        MethodArgumentNotValidException exceptionMock = mock(MethodArgumentNotValidException.class);
        given(exceptionMock.getBindingResult()).willReturn(bindingResultMock);

        ResponseEntity<ExceptionResponse> responseEntity = handler.handleMethodArgumentNotValidException(exceptionMock);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("[MethodArgumentNotValidException error 1, MethodArgumentNotValidException error 2]",
                exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testTransactionSystemException() {
        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleTransactionSystemException(new TransactionSystemException("TransactionSystemException error",
                        new RuntimeException("TransactionSystemException error")));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("An error occurred during transaction processing. Root cause: TransactionSystemException error",
                exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testClientException() {
        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleClientException(new ClientException(HttpStatus.BAD_REQUEST, "ClientException error",
                        new Exception("{\"detail\":\"Invalid route\",\"method\":\"get\",\"status\":404,"
                                + "\"title\":\"Not Found\"," + "\"type\":\"about:blank\","
                                + "\"uri\":\"/metrics/pageviews/aggreg1ate/all-projects/all-1access/all-agents/daily"
                                + "/2015100100/2015103000\"}")));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("ClientException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertInstanceOf(Object.class, exceptionResponse.getCause());
    }

    @Test
    void testClientExceptionWithNoCause() {
        ResponseEntity<ExceptionResponse> responseEntity = handler.handleClientException(
                new ClientException(HttpStatus.BAD_REQUEST, "ClientException error", new Exception("error")));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertNull(exceptionResponse.getCode());
        assertEquals("ClientException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testCommonException() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|%s");

        ResponseEntity<ExceptionResponse> responseEntity = handler.handleCommonException(
                new CommonException(COMMON_ERROR_400_000, new Exception("Exception error"), "CommonException error"),
                response);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("CommonException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testCommonExceptionWithNoArgs() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|%s");

        ResponseEntity<ExceptionResponse> responseEntity = handler.handleCommonException(
                new CommonException(COMMON_ERROR_400_000, new Exception("Exception error")), response);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("null", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testParseCommonExceptionSuccessful() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|Bad Request to user %s");

        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleCommonException(new CommonException(COMMON_ERROR_400_000, "foo"), response);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("Bad Request to user foo", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testParseCommonExceptionNullErrorCode() {
        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleCommonException(new CommonException(ERROR_CODE_NO_KEY), response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: null errorCode", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testParseCommonExceptionMissingProperties() {
        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleCommonException(new CommonException(COMMON_ERROR_500_000), response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: no properties found for COMMON-ERROR-500-000",
                exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testParseCommonExceptionBadMessageProperties() {
        given(env.getProperty(ERROR_CODE_NO_DESC.propertyKey())).willReturn("900|");

        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleCommonException(new CommonException(ERROR_CODE_NO_DESC), response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: badly formatted message 900|", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }

    @Test
    void testParseCommonExceptionBadStatusCodeProperties() {
        given(env.getProperty(ERROR_CODE_NO_CODE.propertyKey())).willReturn("|Other stuff");

        ResponseEntity<ExceptionResponse> responseEntity = handler
                .handleCommonException(new CommonException(ERROR_CODE_NO_CODE), response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());

        ExceptionResponse exceptionResponse = responseEntity.getBody();
        assertNotNull(exceptionResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: could not get http status code from |Other stuff",
                exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
        assertNull(exceptionResponse.getCause());
    }
}

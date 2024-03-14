package com.erebelo.springmongodbdemo.exception;

import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_400_000;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_500_000;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testParseStandardExceptionSuccessfully() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|Bad Request to user %s");

        var exceptionResponse = handler.parseStandardExceptionMessage(new StandardException(COMMON_ERROR_400_000, "foo"), response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("Bad Request to user foo", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseStandardExceptionMessageNullErrorCode() {
        var exceptionResponse = handler.parseStandardExceptionMessage(new StandardException(ERROR_CODE_NO_KEY), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: null errorCode", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseStandardExceptionMessageMissingProperties() {
        var exceptionResponse = handler.parseStandardExceptionMessage(new StandardException(COMMON_ERROR_500_000), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: no properties found for COMMON-ERROR-500-000", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseStandardExceptionBadMessageProperties() {
        given(env.getProperty(ERROR_CODE_NO_DESC.propertyKey())).willReturn("900|");

        var exceptionResponse = handler.parseStandardExceptionMessage(new StandardException(ERROR_CODE_NO_DESC), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: badly formatted message 900|", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testParseStandardExceptionBadStatusCodeProperties() {
        given(env.getProperty(ERROR_CODE_NO_CODE.propertyKey())).willReturn("|Other stuff");

        var exceptionResponse = handler.parseStandardExceptionMessage(new StandardException(ERROR_CODE_NO_CODE), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON_ERROR_500_000", exceptionResponse.getCode());
        assertEquals("Basic error handling failure: could not get http status code from |Other stuff", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testException() {
        var exceptionResponse = handler.exception(new Exception("Exception error"), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-500-000", exceptionResponse.getCode());
        assertEquals("Exception error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testIllegalStateException() {
        var exceptionResponse = handler.illegalStateException(new IllegalStateException("IllegalStateException error"), response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-500-000", exceptionResponse.getCode());
        assertEquals("IllegalStateException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testIllegalArgumentException() {
        var exceptionResponse = handler.illegalArgumentException(new IllegalArgumentException("IllegalArgumentException error"), response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("IllegalArgumentException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testConstraintViolationException() {
        var exceptionMock = mock(ConstraintViolationException.class);
        given(exceptionMock.getMessage()).willReturn("ConstraintViolationException error");

        var exceptionResponse = handler.constraintViolationException(exceptionMock, response);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-422-000", exceptionResponse.getCode());
        assertEquals("ConstraintViolationException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testHttpMessageNotReadableException() {
        var httpInputMessageMock = mock(HttpInputMessage.class);
        var exceptionResponse = handler.httpMessageNotReadableException(
                new HttpMessageNotReadableException("HttpMessageNotReadableException error", httpInputMessageMock), response);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-422-000", exceptionResponse.getCode());
        assertEquals("HttpMessageNotReadableException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
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

        var exceptionResponse = handler.methodArgumentNotValidException(exceptionMock, response);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-422-000", exceptionResponse.getCode());
        assertEquals("[MethodArgumentNotValidException error 1, MethodArgumentNotValidException error 2]", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testStandardException() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|%s");

        var exceptionResponse = handler.standardException(new StandardException(COMMON_ERROR_400_000, new Exception("Exception error"),
                "StandardException error"), response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("StandardException error", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }

    @Test
    void testStandardExceptionWithoutArgs() {
        given(env.getProperty(COMMON_ERROR_400_000.propertyKey())).willReturn("400|%s");

        var exceptionResponse = handler.standardException(new StandardException(COMMON_ERROR_400_000, new Exception("Exception error")), response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
        assertEquals("COMMON-ERROR-400-000", exceptionResponse.getCode());
        assertEquals("null", exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTimestamp());
    }
}

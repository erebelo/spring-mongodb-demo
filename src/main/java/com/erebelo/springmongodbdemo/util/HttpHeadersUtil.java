package com.erebelo.springmongodbdemo.util;

import static com.erebelo.springmongodbdemo.constant.ProfileConstant.LOGGED_IN_USER_ID_HEADER;

import com.erebelo.spring.common.utils.http.HttpTraceHeader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Log4j2
@UtilityClass
public class HttpHeadersUtil {

    public static String getLoggedInUser() {
        return HttpTraceHeader.getHttpServletRequest().getHeader(LOGGED_IN_USER_ID_HEADER);
    }

    public static HttpHeaders getDownstreamApiHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "*");
        httpHeaders.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        return httpHeaders;
    }

    public static HttpHeaders getFileApiResponseHeaders(String filename, long contentLength) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", filename);
        httpHeaders.setContentLength(contentLength);

        return httpHeaders;
    }
}

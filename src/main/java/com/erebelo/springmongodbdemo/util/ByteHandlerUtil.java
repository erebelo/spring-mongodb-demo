package com.erebelo.springmongodbdemo.util;

import com.erebelo.springmongodbdemo.domain.request.ByteWrapperObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ByteHandlerUtil {

    public static ByteWrapperObject byteGenerator(Object obj) {
        log.info("Generating a byte array from object");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return new ByteWrapperObject(baos.toByteArray());
        } catch (IOException e) {
            log.warn("Error generating byte array from object", e);
            return null;
        }
    }

    public static boolean byteArrayComparison(ByteWrapperObject oldObj, ByteWrapperObject newObj) {
        return oldObj != null && newObj != null && Arrays.equals(oldObj.getByteArray(), newObj.getByteArray());
    }
}

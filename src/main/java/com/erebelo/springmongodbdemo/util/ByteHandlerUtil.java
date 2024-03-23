package com.erebelo.springmongodbdemo.util;

import com.erebelo.springmongodbdemo.domain.request.ByteWrapperObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ByteHandlerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ByteHandlerUtil.class);

    public static ByteWrapperObject byteGenerator(Object obj) {
        LOGGER.info("Generating a byte array from object");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return new ByteWrapperObject(baos.toByteArray());
        } catch (IOException e) {
            LOGGER.error("Error generating byte array from object", e);
            return null;
        }
    }

    public static boolean byteArrayComparison(ByteWrapperObject oldObj, ByteWrapperObject newObj) {
        return oldObj != null && newObj != null && Arrays.equals(oldObj.getByteArray(), newObj.getByteArray());
    }
}

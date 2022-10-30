package com.erebelo.springmongodbdemo.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class RegistrationUtilsTest {

    @Test
    void whenTestGetRegistrationIdThenReturnObject() {
        Object result = RegistrationUtils.getRegistrationId();
        then(result).isNotNull();
    }

    @Test
    void whenTestGetRegistrationNameThenReturnObject() {
        Object result = RegistrationUtils.getRegistrationName();
        then(result).isNotNull();
    }
}

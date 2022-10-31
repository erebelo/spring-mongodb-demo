package com.erebelo.springmongodbdemo.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class RegistrationUtilsTest {

    @Test
    void whenTestGetRegistrationNameThenReturnObject() {
        Object result = RegistrationUtils.getRegistrationName();
        then(result).isNotNull();
    }
}

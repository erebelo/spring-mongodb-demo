package com.erebelo.springmongodbdemo.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionProps {

    private String timeout;
    private Read read;

    @Getter
    @Setter
    public static class Read {
        private String timeout;
    }
}

package com.erebelo.springmongodbdemo.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionProps {

    private String timeout;
    private Read read;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Read {
        private String timeout;
    }
}

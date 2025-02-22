package com.erebelo.springmongodbdemo.domain.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkOpsEngineResponse<T> {

    private List<T> success;
    private List<T> failed;

}

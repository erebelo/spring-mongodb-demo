package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.context.history.DocumentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "files")
@DocumentHistory(collection = "files_history")
public class FileEntity extends BaseEntity {

    @Id
    private String id;
    private String name;
    private byte[] data;

}

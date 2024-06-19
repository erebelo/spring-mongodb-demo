package com.erebelo.springmongodbdemo.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedDate
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    private LocalDateTime modifiedDateTime;

    @Version
    private Long version;

}

package com.starcloud.ops.business.app.domain.entity.config;

import lombok.Data;

@Data
public class UserInputFromEntity {

    private String defaultValue;

    private String label;

    private Boolean required;

    private String variable;

    private Integer MaxLength;


}

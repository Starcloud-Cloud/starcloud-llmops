package com.starcloud.ops.business.app.domain.entity.params;

import lombok.Data;

@Data
public class ParamsEntity extends BaseDataEntity {


    private String field;

    private String type;

    private Boolean required;

    private String desc;

    private Object defValue;

    private Boolean extraction;

    private Object testValue;

}

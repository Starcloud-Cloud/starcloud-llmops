package com.starcloud.ops.business.app.util;

import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;

public class ExpandStringSchema extends StringSchema {

    private String expandType;

    public String getExpandType() {
        return expandType;
    }

    public void setExpandType(String expandType) {
        this.expandType = expandType;
    }
}

package com.starcloud.ops.business.dataset.enums;

public enum DocumentSegmentEnum {
    INDEXING("INDEXING"),
    COMPLETED("COMPLETED");

    DocumentSegmentEnum(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

package com.starcloud.ops.business.dataset.enums;

public enum DocumentSegmentEnum {
    SPLIT("SPLIT","拆分完成"),
    INDEXED("INDEXED","索引完成"),
    ERROR("ERROR","");

    DocumentSegmentEnum(String code,String describe) {
        this.code = code;
    }

    private String code;

    private String describe;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

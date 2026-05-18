package com.example.common.enums;

public enum YesOrNo {
    YES(1, "是"),
    NO(0, "否");

    private final Integer code;
    private final String description;

    YesOrNo(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

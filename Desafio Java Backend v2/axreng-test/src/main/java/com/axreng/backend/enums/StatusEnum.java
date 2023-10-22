package com.axreng.backend.enums;

public enum StatusEnum {

    ACTIVE("active"),
    DONE("done");

    private final String value;

    StatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}

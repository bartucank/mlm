package com.metuncc.mlm.exception;

public enum ExceptionCode {
    UNEXPECTED_ERROR("Unexpected error. Please contact with admin."),
    USERNAME_ALREADY_TAKEN("This username already taken."),

    INVALID_REQUEST("Invalid request.");

    private final String description;

    ExceptionCode(String description) {
        this.description = description;
    }



    public String getDescription() {
        return description;
    }
}

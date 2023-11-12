package com.metuncc.mlm.exception;

public enum ExceptionCode {
    UNEXPECTED_ERROR("Unexpected error. Please contact with admin."),
    USERNAME_ALREADY_TAKEN("This username already taken."),
    INVALID_REQUEST("Invalid request."),
    SHELF_NOT_FOUND("Shelf not found."),
    ONLY_METU("Only metuians can register this application."),
    PLEASE_VERIFY_ACCOUNT("Please verify your account via email."),
    BOOK_NOT_FOUND("Book not found."),
    IMAGE_NOT_FOUND("Image not found.");

    private final String description;

    ExceptionCode(String description) {
        this.description = description;
    }



    public String getDescription() {
        return description;
    }
}

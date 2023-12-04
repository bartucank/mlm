package com.metuncc.mlm.exception;

public enum ExceptionCode {
    UNEXPECTED_ERROR("Unexpected error. Please contact with admin."),
    USERNAME_ALREADY_TAKEN("This username already taken."),
    INVALID_REQUEST("Invalid request."),
    SHELF_NOT_FOUND("Shelf not found."),
    ONLY_METU("Only metuians can register this application."),
    PLEASE_VERIFY_ACCOUNT("Please verify your account via email."),
    SESSION_EXPERIED_PLEASE_LOGIN("Session experied. Please log in again."),
    BOOK_NOT_FOUND("Book not found."),
    IMAGE_NOT_FOUND("Image not found."), ROOM_NOT_FOUND("Room not found!"), START_HOUR_INVALID("Start hour should be between 0-23"), END_HOUR_INVALID("End hour should be between 0-23"), SLOT_ON_DAY_EXISTS("TimeSlot already exists. You have to delete or update.");

    private final String description;

    ExceptionCode(String description) {
        this.description = description;
    }



    public String getDescription() {
        return description;
    }
}

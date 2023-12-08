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
    IMAGE_NOT_FOUND("Image not found."),
    ROOM_NOT_FOUND("Room not found!"),
    START_HOUR_INVALID("Start hour should be between 0-23"),
    END_HOUR_INVALID("End hour should be between 0-23"),
    SLOT_ON_DAY_EXISTS("TimeSlot already exists. You have to delete or update."),
    BOOK_NOT_AVAILABLE("Book already taken. User can enter queue with their account."),
    THIS_USER_DID_NOT_TAKE_THIS_BOOK("This user did not take this book."),


    BOOK_NOT_RETURNED_YET("Book not returned yet."),
    BOOK_RESERVED_FOR_SOMEONE("Book is reserved for another person. Please pe patient."),

    USER_NOT_FOUND("User not found.");


    private final String description;

    ExceptionCode(String description) {
        this.description = description;
    }



    public String getDescription() {
        return description;
    }
}

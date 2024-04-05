package com.metuncc.mlm.exception;

public enum ExceptionCode {
    UNEXPECTED_ERROR("Unexpected error. Please contact with admin."),
    USERNAME_ALREADY_TAKEN("This username already taken."),
    INVALID_REQUEST("Invalid request."),
    INVALID_STUDENT_NUMBER("Invalid student number."),
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
    BOOK_ALREADY_ON_USER("This user already borrowed this book."),
    BOOK_RESERVED_FOR_SOMEONE("Book is reserved for another person. Please pe patient."),

    USER_NOT_FOUND("User not found."),
    ROOMSLOT_NOT_FOUND("Room slot not found."),
    ROOMSLOT_NOT_AVAILABLE("Room slot is not available."),
    MAX_RESERVATION_REACHED("Max reservations reached"),
    RESERVATION_NOT_FOUND("Reservation not found"),
    UNAUTHORIZED("Unauthorized."),
    INVALID_CONFIRMATION("Could not confirmed. Please check your room!"),
    ALREADY_IN_QUEUE("You are already in queue."),
    RECEIPT_NOT_FOUND("Receipt not found."),
    COPYCARD_NOT_FOUND("Copy card not found."),
    EMAIL_CANNOT_SEND("Email could not sent."),


    STAR_CANNOT_BE_NULL("Star point cannot be empty."),
    OLD_PASSWORD_CANNOT_BE_NULL("Your current password is incorrect."),
    PASSWORD_CANNOT_BE_NULL("New password cannot be empty."),
    OLD_PASSWORD_INCORRECT("Old password incorrect!"),
    EMAIL_OR_USERNAME_SHOULD_BE_NOT_EMPTY("You have to enter email or username to reset password."),
    COURSE_NAME_CANNOT_BE_NULL("Course name cannot be empty."),

    COURSE_NOT_FOUND("Course not found."),
    COURSE_VISIBILITY_CANNOT_BE_NULL("Course visibility cannot be empty."),
    COURSE_IMAGE_CANNOT_BE_NULL("Course image cannot be empty."), MATERIAL_NOT_FOUND("Material not found."),;





    private final String description;

    ExceptionCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

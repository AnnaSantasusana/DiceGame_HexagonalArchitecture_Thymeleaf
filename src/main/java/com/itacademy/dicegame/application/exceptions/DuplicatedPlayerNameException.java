package com.itacademy.dicegame.application.exceptions;

public class DuplicatedPlayerNameException extends RuntimeException {

    public DuplicatedPlayerNameException(String message) {
        super(message);
    }
}

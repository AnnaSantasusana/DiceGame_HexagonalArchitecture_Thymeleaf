package com.itacademy.dicegame.application.exceptions;

public class EmptyPlayerListException extends RuntimeException {

    public EmptyPlayerListException(String message) {
        super(message);
    }
}

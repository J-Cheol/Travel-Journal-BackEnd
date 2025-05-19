package com.traveljournal.global.exception;

public class BlockBadRequestException extends RuntimeException{
    public BlockBadRequestException(String message) {
        super(message);
    }
}

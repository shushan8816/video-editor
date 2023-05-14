package com.animoto.utils.exceptions;

public class PreconditionFailedException extends Exception {

    public PreconditionFailedException() {
        super();
    }

    public PreconditionFailedException(String message) {
        super(message);
    }
}

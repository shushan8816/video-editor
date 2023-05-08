package com.animoto.utils.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(this.createErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(this.createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(this.createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<?> handleInternalError(InternalErrorException e) {
        return new ResponseEntity<>(this.createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<?> handleDuplicateDataException(DuplicateDataException e) {
        return new ResponseEntity<>(this.createErrorResponse(e.getMessage(), HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExpectationFailedException.class)
    public ResponseEntity<?> handleExpectationFailedException(ExpectationFailedException e) {
        return new ResponseEntity<>(this.createErrorResponse(e.getMessage(), HttpStatus.EXPECTATION_FAILED), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(JwtAuthenticationException e) {
        return new ResponseEntity<>(createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PreconditionFailedException.class)
    public ResponseEntity<?> handlePreconditionFailedException(PreconditionFailedException e) {
        return new ResponseEntity<>(createErrorResponse(e.getMessage(), HttpStatus.PRECONDITION_FAILED), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();

        return new ResponseEntity<>(this.createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> processConversionException(HttpMessageNotReadableException e) {

        String msg = null;
        Throwable cause = e.getCause();

        if (cause instanceof JsonParseException) {
            msg = "Invalid JSON format";
        } else if (cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            if (mie.getPath() != null && mie.getPath().size() > 0) {
                msg = "Invalid request field: " + mie.getPath().get(0).getFieldName();
            } else {
                msg = e.getMessage();
            }
        } else if (cause instanceof JsonMappingException) {
            JsonMappingException jme = (JsonMappingException) cause;
            msg = jme.getOriginalMessage();
            if (jme.getPath() != null && jme.getPath().size() > 0) {
                msg = "Invalid request field: " + jme.getPath().get(0).getFieldName() + ". " + msg;
            }
        }

        return new ResponseEntity<>(createErrorResponse(msg, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        return new ResponseEntity<>(this.createErrorResponse(e.getMessage(), HttpStatus.IM_USED), HttpStatus.IM_USED);
    }

    private Map<String, Object> createErrorResponse(String errorMessage, HttpStatus status) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();

        errorResponse.put("statusCode", status.value());
        errorResponse.put("timestamp", new Date());
        errorResponse.put("error", errorMessage);

        return errorResponse;
    }
}

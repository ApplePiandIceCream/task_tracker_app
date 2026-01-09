package com.dts.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/** global Exception handler for REST API
@RestControllerAdvice means class can handle expections thrown from any @RestController across app
*/

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**Handle exceptions throw when @Valid fails 
    @param ex - The exception containing validation errors 
    @return A map of field names to their specific error messages and 400 BAD_REQUEST status
    */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        ///map to store the errors in the format: {"fieldName": "errorMessage"}
        Map<String, String> errors = new HashMap<>();

        // Extract all field errors collected by the validation result
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        // return map of errors and 400 BAD REQUEST - signlas client side input was invalid
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions when the request body JSON format is unreadable/ incorrect.
     * * @param ex The exception indicates problem with JSON structure or type conversion.
     * @return A map containing a specific error message about the expected date format, 
     * with a 400 BAD_REQUEST status.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFormat(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();

        //specific error message to front end re date format
        error.put("deadline", "Invalid date format. Please use yyyy-MM-dd'T'HH:mm");

        //Return HTTP 400 bad request 
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catch other unexpected runtime exceptions that have not been covered
     * * @param ex - The general exception object
     * @return generic error message with an HTTP 500 INTERNAL_SERVER_ERROR status code- stops server details from being exposed.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralErrors(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

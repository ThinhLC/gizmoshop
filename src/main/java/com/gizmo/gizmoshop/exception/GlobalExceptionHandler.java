package com.gizmo.gizmoshop.exception;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleUsernameNotFound(UsernameNotFoundException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleInvalidInput(InvalidInputException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ResponseWrapper<Void>> handleInvalidTokenException(InvalidTokenException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.UNAUTHORIZED, " Access denied : " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ResponseWrapper<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        // Tạo một đối tượng ResponseWrapper với các tham số hợp lệ
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.FORBIDDEN, "Access denied: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

}

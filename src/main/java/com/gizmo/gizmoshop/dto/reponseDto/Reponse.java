package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reponse<T> {
 private T data;
 private String message;
 private HttpStatus status;
 private LocalDateTime timestamp;
public Reponse(T data, String message, HttpStatus status) {
    this.data = data;
    this.message = message;
    this.status = status;
    this.timestamp = LocalDateTime.now();
}
}

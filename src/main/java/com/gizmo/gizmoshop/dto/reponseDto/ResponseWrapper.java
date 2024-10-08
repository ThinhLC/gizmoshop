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
public class ResponseWrapper<T> {

        private String message;
        private LocalDateTime timestamp;
        private HttpStatus httpStatus;
        private T data;

        public ResponseWrapper( HttpStatus status, String message, T data) {
            this.data = data;
            this.message = message;
            this.httpStatus = httpStatus;
            this.timestamp = LocalDateTime.now();
    }
}

package ch.springframeworkguru.springrestmvc.controller;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;

// Remark. commented out and disabled by the NotfoundException class in adding there @ResponseStatus annotation
//@ControllerAdvice
public class ExceptionHandlerController {

    //@ExceptionHandler(NotfoundException.class)
    @NullMarked
    public ResponseEntity<NotFoundException> handleNotFoundException() {
        return ResponseEntity
            .notFound()
            .build();
    }
}

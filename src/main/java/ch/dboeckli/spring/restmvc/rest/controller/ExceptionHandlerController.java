package ch.dboeckli.spring.restmvc.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;

// Remark. commented out and disabled by the NotfoundException class in adding there @ResponseStatus annotation
//@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    //@ExceptionHandler(NotfoundException.class)
    @NullMarked
    public ResponseEntity<NotFoundException> handleNotFoundException() {
        log.info("handleNotFoundException");
        return ResponseEntity
            .notFound()
            .build();
    }
}

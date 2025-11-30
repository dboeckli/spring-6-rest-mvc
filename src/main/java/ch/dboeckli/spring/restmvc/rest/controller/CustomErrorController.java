package ch.dboeckli.spring.restmvc.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<@NonNull String> handleBindErrors(MethodArgumentNotValidException ex) {
        log.info("handleBindErrors", ex);
        List<Map<String, String>> errorList = ex.getFieldErrors().stream().map(
            fieldError -> {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                return errorMap;
            }
        ).toList();

        return ResponseEntity.badRequest().body(errorList.toString());
    }

}

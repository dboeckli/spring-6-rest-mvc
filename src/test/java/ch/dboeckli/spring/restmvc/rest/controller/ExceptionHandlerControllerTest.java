package ch.dboeckli.spring.restmvc.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlerControllerTest {

    private final ExceptionHandlerController exceptionHandlerController = new ExceptionHandlerController();

    @Test
    void mapsDataIntegrityViolationToConflict() {
        ResponseEntity<ProblemDetail> response = exceptionHandlerController
            .handleDataIntegrityViolation(new DataIntegrityViolationException("foreign key violation"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

}

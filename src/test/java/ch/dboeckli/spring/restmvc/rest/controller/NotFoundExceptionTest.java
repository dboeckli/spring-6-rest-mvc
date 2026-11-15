package ch.dboeckli.spring.restmvc.rest.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundExceptionTest {

    @Test
    void noArgConstructorCreatesExceptionWithoutMessage() {
        NotFoundException exception = new NotFoundException();

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void messageConstructorSetsMessage() {
        NotFoundException exception = new NotFoundException("missing");

        assertThat(exception.getMessage()).isEqualTo("missing");
    }

    @Test
    void messageAndCauseConstructorSetsBoth() {
        IllegalStateException cause = new IllegalStateException("cause");

        NotFoundException exception = new NotFoundException("missing", cause);

        assertThat(exception.getMessage()).isEqualTo("missing");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void causeConstructorSetsCause() {
        IllegalStateException cause = new IllegalStateException("cause");

        NotFoundException exception = new NotFoundException(cause);

        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void fullConstructorSetsAllArguments() {
        IllegalStateException cause = new IllegalStateException("cause");

        NotFoundException exception = new NotFoundException("missing", cause, false, true);

        assertThat(exception.getMessage()).isEqualTo("missing");
        assertThat(exception.getCause()).isSameAs(cause);
        assertThat(exception.getSuppressed()).isEmpty();
    }

}

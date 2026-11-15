package ch.dboeckli.spring.restmvc.rest.controller;

import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomErrorControllerTest {

    private final CustomErrorController controller = new CustomErrorController();

    @Test
    void handleBindErrorsReturnsBadRequestWithFieldErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new BeerDTO(), "beer");
        bindingResult.rejectValue("beerName", "required", "must not be blank");
        MethodParameter parameter = new MethodParameter(
                CustomErrorControllerTest.class.getDeclaredMethod("handleBeer", BeerDTO.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<String> response = controller.handleBindErrors(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("beerName", "must not be blank");
    }

    @SuppressWarnings("unused")
    private void handleBeer(BeerDTO beer) {
    }

}

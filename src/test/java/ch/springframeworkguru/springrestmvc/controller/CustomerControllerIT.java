package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testListCustomer() {
        ResponseEntity<List<CustomerDTO>> customersDtoResponseEntity = customerController.listCustomer();
        List<CustomerDTO> customerDtos = customersDtoResponseEntity.getBody();

        assertAll(
                () -> {
                    assert customerDtos != null;
                    assertEquals(3, customerDtos.size());
                }
        );
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testEmtpyListCustomer() {
        customerRepository.deleteAll();
        ResponseEntity<List<CustomerDTO>> customersDtoResponseEntity = customerController.listCustomer();
        List<CustomerDTO> customerDtos = customersDtoResponseEntity.getBody();

        assertAll(
                () -> {
                    assert customerDtos != null;
                    assertEquals(0, customerDtos.size());
                }
        );
    }

    @Test
    void testGetCustomerById() {
        UUID givenCustomerId = customerRepository.findAll().getFirst().getId();

        ResponseEntity<CustomerDTO> customerDtoResponseEntity = customerController.getCustomerById(givenCustomerId);
        CustomerDTO customerDTO = customerDtoResponseEntity.getBody();

        assertAll(
                () -> {
                    assert customerDTO != null;
                    assertEquals(givenCustomerId, customerDTO.getId());
                }
        );
    }

    @Test
    void testGetCustomerByIdNotFound() {
        assertThrows(NotfoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
    }
}
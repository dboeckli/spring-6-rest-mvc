package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.entity.Customer;
import ch.springframeworkguru.springrestmvc.mapper.CustomerMapper;
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
    
    @Autowired
    CustomerMapper customerMapper;
    
    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testDeleteCustomer() {
        UUID givenCustomerId = customerRepository.findAll().getFirst().getId();
        
        customerController.deleteCustomer(givenCustomerId);

        assertFalse(customerRepository.findById(givenCustomerId).isPresent());
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testDeleteCustomerDoesNotExist() {
        assertThrows(NotfoundException.class, () -> {
            customerController.deleteCustomer(UUID.randomUUID());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testUpdateCustomer() {
        Customer givenCustomer = customerRepository.findAll().getFirst();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(givenCustomer);
        customerDTO.setCustomerName("Hans");

        CustomerDTO editedCustomerDTO = customerController.editCustomer(customerDTO, customerDTO.getId()).getBody();

        assertAll(() -> {
            assertNotNull(editedCustomerDTO);
            assertEquals("Hans", editedCustomerDTO.getCustomerName());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testUpdateCustomerDoesNotExist() {
        CustomerDTO customerDTO = CustomerDTO.builder().build();

        assertThrows(NotfoundException.class, () -> {
            customerController.editCustomer(customerDTO, UUID.randomUUID());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testPatchCustomer() {
        Customer givenCustomer = customerRepository.findAll().getFirst();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(givenCustomer);
        customerDTO.setCustomerName("Hans");

        CustomerDTO editedCustomerDTO = customerController.patchCustomer(customerDTO, customerDTO.getId()).getBody();

        assertAll(() -> {
            assertNotNull(editedCustomerDTO);
            assertEquals("Hans", editedCustomerDTO.getCustomerName());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testPatchCustomerDoesNotExist() {
        CustomerDTO customerDTO = CustomerDTO.builder().build();

        assertThrows(NotfoundException.class, () -> {
            customerController.patchCustomer(customerDTO, UUID.randomUUID());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testCreateCustomer() {
        CustomerDTO customerDTO = CustomerDTO.builder()
            .customerName("Fridolin")
            .build();

        CustomerDTO createdCustomer = customerController.createCustomer(customerDTO).getBody();

        assertAll(() -> {
            assertNotNull(createdCustomer);
            assertEquals("Fridolin", createdCustomer.getCustomerName());
        });
    }

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
    @Rollback(true) // we roll back the deletion to assure that the other tests are not failing
    void testEmptyListCustomer() {
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

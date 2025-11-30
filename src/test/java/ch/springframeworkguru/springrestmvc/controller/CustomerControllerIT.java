package ch.springframeworkguru.springrestmvc.controller;

import ch.guru.springframework.spring6restmvcapi.dto.CustomerDTO;
import ch.springframeworkguru.springrestmvc.entity.Customer;
import ch.springframeworkguru.springrestmvc.mapper.CustomerMapper;
import ch.springframeworkguru.springrestmvc.repository.BeerOrderRepository;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Test
    @Transactional
    @Rollback(true)
        // we roll back to deletion to assure that the other tests are not failing
    void testDeleteCustomer() {
        UUID givenCustomerId = customerRepository.findAll().getFirst().getId();

        customerController.deleteCustomer(givenCustomerId);

        assertFalse(customerRepository.findById(givenCustomerId).isPresent());
    }

    @Test
    @Transactional
    @Rollback(true)
        // we rollback to deletion to assuere that the other tests are not failling
    void testDeleteCustomerDoesNotExist() {
        assertThrows(NotFoundException.class, () -> customerController.deleteCustomer(UUID.randomUUID()));
    }

    @Test
    @Transactional
    @Rollback(true)
        // we roll back to deletion to assure that the other tests are not failing
    void testUpdateCustomer() {
        Customer givenCustomer = customerRepository.findAll().getFirst();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(givenCustomer);
        customerDTO.setName("Hans");

        CustomerDTO editedCustomerDTO = customerController.editCustomer(customerDTO, customerDTO.getId()).getBody();

        assertAll(() -> {
            assertNotNull(editedCustomerDTO);
            assertEquals("Hans", editedCustomerDTO.getName());
        });
    }

    @Test
    @Transactional
    @Rollback(true)
        // we roll back to deletion to assure that the other tests are not failing
    void testUpdateCustomerDoesNotExist() {
        CustomerDTO customerDTO = CustomerDTO.builder().build();

        assertThrows(NotFoundException.class, () -> customerController.editCustomer(customerDTO, UUID.randomUUID()));
    }

    @Test
    @Transactional
    @Rollback(true)
        // we roll back to deletion to assure that the other tests are not failing
    void testPatchCustomer() {
        Customer givenCustomer = customerRepository.findAll().getFirst();

        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(givenCustomer);
        customerDTO.setName("Hans");

        CustomerDTO editedCustomerDTO = customerController.patchCustomer(customerDTO, customerDTO.getId()).getBody();
        assertAll(() -> {
            assertNotNull(editedCustomerDTO);
            assertEquals("Hans", editedCustomerDTO.getName());
        });
    }

    @Test
    @Transactional
    @Rollback(true)
        // we roll back to deletion to assure that the other tests are not failing
    void testPatchCustomerDoesNotExist() {
        CustomerDTO customerDTO = CustomerDTO.builder().build();

        assertThrows(NotFoundException.class, () -> customerController.patchCustomer(customerDTO, UUID.randomUUID()));
    }

    @Test
    @Transactional
    @Rollback(true)
        // we roll back to deletion to assure that the other tests are not failing
    void testCreateCustomer() {
        CustomerDTO customerDTO = CustomerDTO.builder()
            .name("Fridolin")
            .build();

        CustomerDTO createdCustomer = customerController.createCustomer(customerDTO).getBody();

        assertAll(() -> {
            assertNotNull(createdCustomer);
            assertEquals("Fridolin", createdCustomer.getName());
        });
    }

    @Test
    void testListCustomer() {
        ResponseEntity<@NonNull List<@NonNull CustomerDTO>> customersDtoResponseEntity = customerController.listCustomer();
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
    @Rollback(true)
        // we roll back the deletion to assure that the other tests are not failing
    void testEmptyListCustomer() {
        beerOrderRepository.deleteAll();
        customerRepository.deleteAll();

        // we need to clear the cache, because the deleteAll (in the repository class) does not evict the cache
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(cacheName -> cacheManager.getCache(cacheName).clear());

        ResponseEntity<@NonNull List<@NonNull CustomerDTO>> customersDtoResponseEntity = customerController.listCustomer();
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
        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
    }
}

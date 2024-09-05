package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.model.Customer;
import ch.springframeworkguru.springrestmvc.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @DeleteMapping(value = "/deleteCustomer/{customerId}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable("customerId") UUID customerId) {
        Customer deletedCustomer = customerService.deleteCustomer(customerId);
        return new ResponseEntity<>(deletedCustomer, HttpStatus.FOUND);
    }

    @RequestMapping(value = "/listCustomer",
            method = RequestMethod.GET)
    public List<Customer> listCustomer() {
        return customerService.listCustomers();
    }

    @RequestMapping(value = "/getCustomerById/{customerId}",
            method = RequestMethod.GET)
    public Customer getCustomerById(@PathVariable("customerId") UUID customerId) {
        log.debug("Get Customer by Id - in controller");
        return customerService.getCustomerById(customerId);
    }

    @PostMapping(value = "/createCustomer")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer newCustomer) {
        Customer customer = customerService.saveNewCustomer(newCustomer);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/editCustomer/{customerId}",
            method = RequestMethod.PUT)
    public ResponseEntity<Customer> editCustomer(@RequestBody Customer customerToEdit, @PathVariable("customerId") UUID customerId) {
        Customer customer = customerService.editCustomer(customerId, customerToEdit);
        return new ResponseEntity<>(customer, HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/patchCustomer/{customerId}")
    public ResponseEntity<Customer> patchCustomer(@RequestBody Customer customer, @PathVariable("customerId") UUID customerId) {
        Customer patchedCustomer = customerService.patchCustomer(customerId, customer);
        return new ResponseEntity<>(patchedCustomer, HttpStatus.NO_CONTENT);
    }
}

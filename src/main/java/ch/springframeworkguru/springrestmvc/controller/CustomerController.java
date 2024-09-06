package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.service.CustomerService;
import ch.springframeworkguru.springrestmvc.service.dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${controllers.customer-controller.request-path}")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @DeleteMapping(value = "/deleteCustomer/{customerId}")
    public ResponseEntity<CustomerDTO> deleteCustomer(@PathVariable("customerId") UUID customerId) {
        CustomerDTO deletedCustomer = customerService.deleteCustomer(customerId);
        return new ResponseEntity<>(deletedCustomer, HttpStatus.OK);
    }

    @GetMapping(value = "/listCustomer")
    public List<CustomerDTO> listCustomer() {
        return customerService.listCustomers();
    }

    @GetMapping(value = "/getCustomerById/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("customerId") UUID customerId) {
        return new ResponseEntity<>(customerService.getCustomerById(customerId).orElseThrow(NotfoundException::new), HttpStatus.OK);
    }

    @PostMapping(value = "/createCustomer")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO newCustomer) {
        CustomerDTO customer = customerService.saveNewCustomer(newCustomer);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @PutMapping(value = "/editCustomer/{customerId}")
    public ResponseEntity<CustomerDTO> editCustomer(@RequestBody CustomerDTO customerToEdit, @PathVariable("customerId") UUID customerId) {
        CustomerDTO customer = customerService.editCustomer(customerId, customerToEdit);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @PatchMapping(value = "/patchCustomer/{customerId}")
    public ResponseEntity<CustomerDTO> patchCustomer(@RequestBody CustomerDTO customer, @PathVariable("customerId") UUID customerId) {
        CustomerDTO patchedCustomer = customerService.patchCustomer(customerId, customer);
        return new ResponseEntity<>(patchedCustomer, HttpStatus.OK);
    }
}

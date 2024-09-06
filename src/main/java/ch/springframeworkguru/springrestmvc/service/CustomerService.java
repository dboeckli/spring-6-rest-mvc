package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    List<Customer> listCustomers();

    Optional<Customer> getCustomerById(UUID id);

    Customer saveNewCustomer(Customer newCustomer);

    Customer editCustomer(UUID customerId, Customer customerToEdit);

    Customer deleteCustomer(UUID customerId);

    Customer patchCustomer(UUID customerId, Customer customer);
}

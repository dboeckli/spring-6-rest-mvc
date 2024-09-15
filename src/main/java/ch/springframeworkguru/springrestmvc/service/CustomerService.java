package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.service.dto.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    List<CustomerDTO> listCustomers();

    Optional<CustomerDTO> getCustomerById(UUID id);

    CustomerDTO saveNewCustomer(CustomerDTO newCustomer);

    Optional<CustomerDTO> editCustomer(UUID customerId, CustomerDTO customerToEdit);

    Boolean deleteCustomer(UUID customerId);

    Optional<CustomerDTO> patchCustomer(UUID customerId, CustomerDTO customer);
}

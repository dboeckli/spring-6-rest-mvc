package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.mapper.CustomerMapper;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class CustomerServiceJpaImpl implements CustomerService {

    CustomerRepository customerRepository;

    CustomerMapper customerMapper;

    public CustomerServiceJpaImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository
                .findAll()
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.ofNullable(customerMapper
                .customerToCustomerDto(customerRepository
                        .findById(id)
                        .orElse(null)));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO newCustomer) {
        return null;
    }

    @Override
    public CustomerDTO editCustomer(UUID customerId, CustomerDTO customerToEdit) {
        return null;
    }

    @Override
    public CustomerDTO deleteCustomer(UUID customerId) {
        return null;
    }

    @Override
    public CustomerDTO patchCustomer(UUID customerId, CustomerDTO customer) {
        return null;
    }
}

package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.mapper.CustomerMapper;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
    @Cacheable(cacheNames = "customerListCache")
    public List<CustomerDTO> listCustomers() {
        return customerRepository
                .findAll()
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "customerCache")
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.ofNullable(customerMapper
                .customerToCustomerDto(customerRepository
                        .findById(id)
                        .orElse(null)));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO newCustomer) {
        return customerMapper.customerToCustomerDto(customerRepository.save(customerMapper.customerDtoToCustomer(newCustomer)));
    }

    @Override
    public Optional<CustomerDTO> editCustomer(UUID customerId, CustomerDTO customerToEdit) {
        customerRepository.findById(customerId).ifPresent(foundCustomer -> {
            if (StringUtils.hasText(customerToEdit.getCustomerName())) {
                foundCustomer.setCustomerName(customerToEdit.getCustomerName());
            }
            foundCustomer.setLastModifiedDate(LocalDateTime.now());
            customerMapper.customerToCustomerDto(customerRepository.save(foundCustomer));
        });
        return Optional.ofNullable(customerMapper.customerToCustomerDto(customerRepository.findById(customerId).orElse(null)));
    }

    @Override
    public Optional<CustomerDTO> patchCustomer(UUID customerId, CustomerDTO customerToPatch) {
        customerRepository.findById(customerId).ifPresent(foundCustomer -> {
            if (StringUtils.hasText(customerToPatch.getCustomerName())) {
                foundCustomer.setCustomerName(customerToPatch.getCustomerName());
            }
            foundCustomer.setLastModifiedDate(LocalDateTime.now());
            customerMapper.customerToCustomerDto(customerRepository.save(foundCustomer));
        });
        return Optional.ofNullable(customerMapper.customerToCustomerDto(customerRepository.findById(customerId).orElse(null)));
    }

    @Override
    public Boolean deleteCustomer(UUID customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }
}

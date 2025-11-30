package ch.springframeworkguru.springrestmvc.service;

import ch.guru.springframework.spring6restmvcapi.dto.CustomerDTO;
import ch.springframeworkguru.springrestmvc.mapper.CustomerMapper;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    CacheManager cacheManager;

    public CustomerServiceJpaImpl(CustomerRepository customerRepository, CustomerMapper customerMapper, CacheManager cacheManager) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.cacheManager = cacheManager;
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
    @Caching(evict = {
        @CacheEvict(cacheNames = "customerCache"),
        @CacheEvict(cacheNames = "customerListCache")
    })
    public CustomerDTO saveNewCustomer(CustomerDTO newCustomer) {
        if (cacheManager.getCache("customerListCache") != null) {
            cacheManager.getCache("customerListCache").clear();
        }
        cacheManager.getCache("customerListCache").clear();
        return customerMapper.customerToCustomerDto(customerRepository.save(customerMapper.customerDtoToCustomer(newCustomer)));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "customerCache"),
        @CacheEvict(cacheNames = "customerListCache")
    })
    public Optional<CustomerDTO> editCustomer(UUID customerId, CustomerDTO customerToEdit) {
        clearCache(customerId);

        customerRepository.findById(customerId).ifPresent(foundCustomer -> {
            if (StringUtils.hasText(customerToEdit.getName())) {
                foundCustomer.setName(customerToEdit.getName());
            }
            foundCustomer.setUpdateDate(LocalDateTime.now());
            customerMapper.customerToCustomerDto(customerRepository.save(foundCustomer));
        });
        return Optional.ofNullable(customerMapper.customerToCustomerDto(customerRepository.findById(customerId).orElse(null)));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "customerCache"),
        @CacheEvict(cacheNames = "customerListCache")
    })
    public Optional<CustomerDTO> patchCustomer(UUID customerId, CustomerDTO customerToPatch) {
        clearCache(customerId);

        customerRepository.findById(customerId).ifPresent(foundCustomer -> {
            if (StringUtils.hasText(customerToPatch.getName())) {
                foundCustomer.setName(customerToPatch.getName());
            }
            foundCustomer.setUpdateDate(LocalDateTime.now());
            customerMapper.customerToCustomerDto(customerRepository.save(foundCustomer));
        });
        return Optional.ofNullable(customerMapper.customerToCustomerDto(customerRepository.findById(customerId).orElse(null)));
    }

    @Override
    // Eviction does not work. We are using explicitly the cachemanager
    @Caching(evict = {
        @CacheEvict(cacheNames = "customerCache"),
        @CacheEvict(cacheNames = "customerListCache")
    })
    public Boolean deleteCustomer(UUID customerId) {
        if (customerRepository.existsById(customerId)) {

            this.clearCache(customerId);

            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }

    private void clearCache(UUID customerId) {
        if (cacheManager.getCache("customerCache") != null) {
            cacheManager.getCache("customerCache").evict(customerId);
        }
        if (cacheManager.getCache("customerListCache") != null) {
            cacheManager.getCache("customerListCache").clear();
        }
    }
}

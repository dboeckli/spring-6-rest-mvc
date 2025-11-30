package ch.dboeckli.spring.restmvc.repository;

import ch.dboeckli.spring.restmvc.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}

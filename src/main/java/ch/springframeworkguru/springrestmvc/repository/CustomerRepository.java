package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}

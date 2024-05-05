package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerCode(String aid);

    Optional<Customer> findByKseiSafeCode(String kseiSafeCode);

    List<Customer> findByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
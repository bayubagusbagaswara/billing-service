package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Boolean existsByCustomerCode(String code);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM customer WHERE customer_code = :customerCode AND (:subCode IS NULL OR sub_code = :subCode)", nativeQuery = true)
    int countByCustomerCodeAndOptionalSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);

    Optional<Customer> findByCustomerCode(String code);

    Optional<Customer> findByKseiSafeCode(String kseiSafeCode);

    @Query(value = "SELECT * FROM customer WHERE billing_category = :category AND billing_type = :type", nativeQuery = true)
    List<Customer> findAllByBillingCategoryAndBillingType(@Param("category") String billingCategory, @Param("type") String billingType);

    @Query(value = "SELECT * FROM customer WHERE customer_code = :customerCode AND (:subCode IS NULL OR sub_code = :subCode)", nativeQuery = true)
    Optional<Customer> findByCustomerCodeAndOptionalSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.customerCode) = LOWER(:customerCode) AND LOWER(COALESCE(c.subCode, '')) = LOWER(COALESCE(:subCode, ''))")
    boolean existsCustomerByCustomerCodeAndSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);

}

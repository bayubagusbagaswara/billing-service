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

    @Query(value = "select case when count(c)> 0 then true else false end from BillingCustomer c where lower(c.customerCode) = lower(:customerCode) AND lower(COALESCE(c.subCode,'')) = lower(COALESCE(:subCode, ''))")
    boolean existsCustomerByCustomerCodeAndSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);

    Optional<Customer> findByCustomerCode(String code);

    Optional<Customer> findByKseiSafeCode(String kseiSafeCode);

    @Query(value = "SELECT * FROM billing_customer WHERE billing_category = :category AND billing_type = :type", nativeQuery = true)
    List<Customer> findAllByBillingCategoryAndBillingType(String billingCategory, String billingType);

    // Mengambil data dengan kondisi subCode bisa kosong (null)
//    @Query(value = "SELECT c FROM Customer c WHERE c.customerCode = :customerCode AND (:subCode IS NULL OR c.subCode = :subCode)")
//    Optional<Customer> findByCustomerCodeAndOptionalSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);

    // Menggunakan native query untuk mengambil data dengan kondisi subCode bisa kosong (null)
    @Query(value = "SELECT * FROM billing_customer WHERE customer_code = :customerCode AND (:subCode IS NULL OR sub_code = :subCode)", nativeQuery = true)
    Optional<Customer> findByCustomerCodeAndOptionalSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);


}

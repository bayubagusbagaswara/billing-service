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

//    @Query(value = "select case when count(c)> 0 then true else false end from Customer c where lower(c.customerCode) = lower(:customerCode) AND lower(COALESCE(c.subCode,'')) = lower(COALESCE(:subCode, ''))")
//    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
//        "FROM customer " +
//        "WHERE LOWER(customer_code) = LOWER(:customerCode) " +
//        "AND LOWER(COALESCE(sub_code, '')) = LOWER(COALESCE(:subCode, ''))",
//        nativeQuery = true)
//    Boolean existsCustomerByCustomerCodeAndSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);
//
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM customer WHERE customer_code = :customerCode AND (:subCode IS NULL OR sub_code = :subCode)", nativeQuery = true)
    int countByCustomerCodeAndOptionalSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);

    Optional<Customer> findByCustomerCode(String code);

    Optional<Customer> findByKseiSafeCode(String kseiSafeCode);

    @Query(value = "SELECT * FROM customer WHERE billing_category = :category AND billing_type = :type", nativeQuery = true)
    List<Customer> findAllByBillingCategoryAndBillingType(@Param("category") String billingCategory, @Param("type") String billingType);

    // Mengambil data dengan kondisi subCode bisa kosong (null)
//    @Query(value = "SELECT c FROM Customer c WHERE c.customerCode = :customerCode AND (:subCode IS NULL OR c.subCode = :subCode)")
//    Optional<Customer> findByCustomerCodeAndOptionalSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);

    // Menggunakan native query untuk mengambil data dengan kondisi subCode bisa kosong (null)
    @Query(value = "SELECT * FROM customer WHERE customer_code = :customerCode AND (:subCode IS NULL OR sub_code = :subCode)", nativeQuery = true)
    Optional<Customer> findByCustomerCodeAndOptionalSubCode(@Param("customerCode") String customerCode, @Param("subCode") String subCode);


}

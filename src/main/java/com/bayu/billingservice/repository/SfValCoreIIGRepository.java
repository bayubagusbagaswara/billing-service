package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValCoreIIG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SfValCoreIIGRepository extends JpaRepository<SfValCoreIIG, Long> {

    List<SfValCoreIIG> findAllByCustomerCode(String customerCode);

    @Query(value = "SELECT * FROM sf_val_core_iig WHERE customer_code = ?1 ORDER BY date ASC", nativeQuery = true)
    List<SfValCoreIIG> findAllByCustomerCodeOrderByDateAsc(String customerCode);
}

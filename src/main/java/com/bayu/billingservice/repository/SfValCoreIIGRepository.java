package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValCoreIIG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SfValCoreIIGRepository extends JpaRepository<SfValCoreIIG, Long> {

    List<SfValCoreIIG> findAllByCustomerCode(String customerCode);

}

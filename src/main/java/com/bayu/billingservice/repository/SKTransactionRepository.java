package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SkTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SKTransactionRepository extends JpaRepository<SkTransaction, Long> {

}

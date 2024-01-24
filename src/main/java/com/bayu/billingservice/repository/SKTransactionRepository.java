package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SKTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SKTransactionRepository extends JpaRepository<SKTransaction, Long> {

}

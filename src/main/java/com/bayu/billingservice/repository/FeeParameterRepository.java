package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.FeeParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeParameterRepository extends JpaRepository<FeeParameter, Long> {

    Optional<FeeParameter> findByName(String name);

}

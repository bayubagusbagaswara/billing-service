package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.GLCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GLCreditRepository extends JpaRepository<GLCredit, Long> {

    @Query(value = "SELECT * FROM gl_credit WHERE gl_billing_template = :billTemplate ", nativeQuery = true)
    GLCredit findByGlBillingTemplate(@Param("billTemplate") String billingTemplate);

    @Query(value = "SELECT * FROM gl_credit WHERE gl_billing_template = :billTemplate ORDER BY journal_sequence ASC", nativeQuery = true)
    List<GLCredit> findAllByGlBillingTemplate(@Param("billTemplate") String billingTemplate);

    @Query(value = "SELECT * FROM gl_credit WHERE gl_billing_template = :billingTemplate " +
            "AND gl_credit_name = :glCreditName", nativeQuery = true)
    Optional<GLCredit> findByGlBillingTemplateAndGlCreditName(
            @Param("billingTemplate") String billingTemplate,
            @Param("glCreditName") String glCreditName
    );

}

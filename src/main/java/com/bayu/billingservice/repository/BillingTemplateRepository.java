package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BillingTemplateRepository extends JpaRepository<BillingTemplate, Long> {

    @Query(value = "SELECT * FROM billing_template WHERE category = :category AND type = :type AND (:subCode IS NULL OR sub_code = :subCode)", nativeQuery = true)
    Optional<BillingTemplate> findByCategoryAndTypeAndSubCode(@Param("category") String category, @Param("type") String type, @Param("subCode") String subCode);

//    @Query(value = "SELECT EXISTS (SELECT 1 FROM billing_template WHERE category = :category AND type = :type AND (:subCode IS NULL OR sub_code = :subCode))", nativeQuery = true)
//    boolean existsByCategoryAndTypeAndSubCode(@Param("category") String category, @Param("type") String type, @Param("subCode") String subCode);

    @Query(value = "select case when count(c) > 0 then true else false end from BillingTemplate c " +
            "WHERE lower(c.category) = lower(:category) " +
            "AND lower(c.type) = lower(:type) " +
            "AND lower(c.currency) = lower(:currency) " +
            "AND lower(c.templateName) = lower(:templateName) " +
            "AND lower(COALESCE(c.subCode,'')) = lower(COALESCE(:subCode, ''))")
    boolean existsByCategoryAndTypeAndCurrencyAndSubCodeAndTemplateName(
            @Param("category") String category,
            @Param("type") String type,
            @Param("currency") String currency,
            @Param("subCode") String subCode,
            @Param("templateName") String templateName
    );
}

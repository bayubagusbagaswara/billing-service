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

    // lower(COALESCE(c.subCode, '')) = lower(COALESCE(:subCode, ''))
    @Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM BillingTemplate c " +
            "WHERE LOWER(c.category) = LOWER(:category) " +
            "AND LOWER(c.type) = LOWER(:type) " +
            "AND LOWER(c.currency) = LOWER(:currency) " +
            "AND LOWER(c.templateName) = LOWER(:templateName) " +
            "AND (LOWER(c.subCode) = LOWER(:subCode) OR (c.subCode IS NULL AND :subCode IS NULL))")
    boolean existsByCategoryAndTypeAndCurrencyAndSubCodeAndTemplateName(
            @Param("category") String category,
            @Param("type") String type,
            @Param("currency") String currency,
            @Param("subCode") String subCode,
            @Param("templateName") String templateName
    );
}

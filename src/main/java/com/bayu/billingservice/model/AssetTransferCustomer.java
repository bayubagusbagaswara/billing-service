package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.Approval;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "asset_transfer_customer")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssetTransferCustomer extends Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "security_code")
    private String securityCode;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "effective_date")
    private String effectiveDate;

    @Column(name = "transfer_asset_type")
    private String transferAssetType;

    @Column(name = "is_enable")
    private boolean isEnable;
}

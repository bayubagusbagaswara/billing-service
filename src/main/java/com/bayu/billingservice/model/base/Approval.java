package com.bayu.billingservice.model.base;

import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@MappedSuperclass
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class Approval {

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @Column(name = "input_id")
    private String inputId;

    @Column(name = "input_ip_address")
    private String inputIPAddress;

    @Column(name = "input_date")
    private Date inputDate;

    @Column(name = "approve_id")
    private String approveId;

    @Column(name = "approve_ip_address")
    private String approveIPAddress;

    @Column(name = "approve_date")
    private Date approveDate;
}

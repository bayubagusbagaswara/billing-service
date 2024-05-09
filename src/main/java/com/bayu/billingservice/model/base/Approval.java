package com.bayu.billingservice.model.base;

import jakarta.persistence.Column;
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

    @Column(name = "input_id")
    private String inputId;

    @Column(name = "input_ip_address")
    private String inputIPAddress;

    @Column(name = "input_date")
    private Date inputDate;

    @Column(name = "approval_id")
    private String approvalId;

    @Column(name = "approval_ip_address")
    private String approvalIPAddress;

    @Column(name = "approval_date")
    private Date approvalDate;
}

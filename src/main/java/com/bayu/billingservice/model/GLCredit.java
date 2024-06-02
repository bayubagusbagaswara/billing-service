package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gl_credit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GLCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gl_billing_template")
    private String glBillingTemplate;

    @Column(name = "gl_credit_name")
    private String glCreditName;

    @Column(name = "gl_credit_account_value")
    private int glCreditAccountValue;

    @Column(name = "journal_type")
    private String journalType;

    @Column(name = "journal_sequence")
    private int journalSequence;

}

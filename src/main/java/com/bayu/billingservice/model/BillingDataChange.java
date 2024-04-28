package com.bayu.billingservice.model;

import com.bayu.billingservice.model.enumerator.ActionStatus;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Entity
@Table(name = "billing_data_changes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDataChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @Column(name = "input_id")
    private String inputId;

    @Column(name = "input_date")
    private Date inputDate;

    @Column(name = "input_ip_address")
    private String inputIPAddress;

    @Column(name = "approve_id")
    private String approveId;

    @Column(name = "approve_date")
    private Date approveDate;

    @Column(name = "approve_ip_address")
    private String approveIPAddress;

    @Enumerated(EnumType.STRING)
    private ActionStatus actionStatus;

    @Column(name = "entity_class_name")
    private String entityClassName;

    @Column(name = "table_name")
    private String tableName;

    @Lob
    @Column(name = "json_date_before")
    private String jsonDataBefore;

    @Lob
    @Column(name = "json_data_after")
    private String jsonDataAfter;

    @Column(name = "description")
    private String description;

    private String getIP() {
        String ipStr = "";
        InetAddress ip;

        try {
            ip = InetAddress.getLocalHost();
            ipStr = ip.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Error get IP Address");
        }
        return ipStr;
    }

}

package com.bayu.billingservice.model;

import com.bayu.billingservice.model.enumerator.ChangeAction;
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
@Table(name = "data_change")
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
    private ChangeAction changeAction;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "entity_class_name")
    private String entityClassName;

    @Column(name = "table_name")
    private String tableName;

//    @Column(name = "json_data_before", columnDefinition = "CLOB") // Untuk Oracle
//    // @Column(name = "json_data_before", columnDefinition = "TEXT") // Untuk MySQL
    @Lob
    @Column(name = "json_date_before", columnDefinition = "TEXT")
    private String jsonDataBefore;

    @Lob
    @Column(name = "json_data_after", columnDefinition = "TEXT")
    private String jsonDataAfter;

    @Column(name = "description")
    private String description;

    @Column(name = "method")
    private String methodHttp;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "is_request_body")
    private Boolean isRequestBody;

    @Column(name = "is_request_param")
    private Boolean isRequestParam;

    @Column(name = "is_path_variable")
    private Boolean isPathVariable;

    @Column(name = "menu")
    private String menu;

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

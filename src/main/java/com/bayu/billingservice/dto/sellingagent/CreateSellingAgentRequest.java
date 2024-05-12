package com.bayu.billingservice.dto.sellingagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSellingAgentRequest {

    private String inputId;

    private String inputIPAddress;

    private String code;

    private String name;

    private String gl;

    private String glName;

    private String account;

    private String accountName;

    private String email;

    private String address;

    private String description;
}

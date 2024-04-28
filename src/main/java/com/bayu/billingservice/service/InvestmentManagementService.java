package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListRequest;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListResponse;

public interface InvestmentManagementService {

    // check existing with code
    Boolean checkExistByCode(String code);

    // create single data

    // create with upload data list
    CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest);

    // approve upload data list
    CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest);

    // get by code

    // get all

    // update by id

    // update with upload data list

    // delete by id
}

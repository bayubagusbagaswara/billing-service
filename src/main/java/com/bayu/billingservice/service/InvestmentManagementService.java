package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListRequest;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListResponse;
import com.bayu.billingservice.dto.investmentmanagement.UpdateInvestmentManagementListRequest;
import com.bayu.billingservice.dto.investmentmanagement.UpdateInvestmentManagementListResponse;

public interface InvestmentManagementService {

    // check existing with code
    boolean isCodeAlreadyExists(String code);

    // create single data

    // create with upload data list
    CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest);

    // approve upload data list
    CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest);

    // get by code

    // get all

    // update by id

    // update with upload data list
    UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest investmentManagementListRequest);


    UpdateInvestmentManagementListResponse updateListApprove(UpdateInvestmentManagementListRequest investmentManagementListRequest);


    // delete by id
}

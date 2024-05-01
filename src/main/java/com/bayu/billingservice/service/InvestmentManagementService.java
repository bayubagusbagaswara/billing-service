package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.model.InvestmentManagement;

public interface InvestmentManagementService {

    // check existing with code
    boolean isCodeAlreadyExists(String code);

    // create single data
    CreateInvestmentManagementListResponse create(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);

    // create with upload data list
    CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO);

    // approve upload data list
    CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest);

    InvestmentManagement getById(Long id);
    // get by code

    // get all

    // update by id
    UpdateInvestmentManagementListResponse updateById(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);


    // update with upload data list
    UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO);


    UpdateInvestmentManagementListResponse updateListApprove(UpdateInvestmentManagementListRequest investmentManagementListRequest);



    // delete by id
}

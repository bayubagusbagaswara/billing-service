package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.model.InvestmentManagement;

public interface InvestmentManagementService {

    boolean isCodeAlreadyExists(String code);

    CreateInvestmentManagementListResponse create(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO);
    CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest);

    InvestmentManagement getById(Long id);

    UpdateInvestmentManagementListResponse updateById(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO);
    UpdateInvestmentManagementListResponse updateListApprove(UpdateInvestmentManagementListRequest investmentManagementListRequest);



    // delete by id
}

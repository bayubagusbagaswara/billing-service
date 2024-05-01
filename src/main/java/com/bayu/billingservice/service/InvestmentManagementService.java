package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;

public interface InvestmentManagementService {

    boolean isCodeAlreadyExists(String code);

    CreateInvestmentManagementListResponse create(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO);
    CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest);

    UpdateInvestmentManagementListResponse updateById(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO);
    UpdateInvestmentManagementListResponse updateListApprove(UpdateInvestmentManagementListRequest investmentManagementListRequest);

    DeleteInvestmentManagementListResponse deleteList(DeleteInvestmentManagementListRequest request, BillingDataChangeDTO dataChangeDTO);
    DeleteInvestmentManagementListResponse deleteListApprove(DeleteInvestmentManagementListRequest request);

}

package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;

import java.util.List;

public interface InvestmentManagementService {

    boolean isCodeAlreadyExists(String code);

    CreateInvestmentManagementListResponse createSingleData(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    CreateInvestmentManagementListResponse createMultipleData(CreateInvestmentManagementListRequest requestList, BillingDataChangeDTO dataChangeDTO);
    CreateInvestmentManagementListResponse createMultipleApprove(CreateInvestmentManagementListRequest requestList);

    UpdateInvestmentManagementListResponse updateSingleData(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    UpdateInvestmentManagementListResponse updateMultipleData(UpdateInvestmentManagementListRequest requestList, BillingDataChangeDTO dataChangeDTO);
    UpdateInvestmentManagementListResponse updateMultipleApprove(UpdateInvestmentManagementListRequest request);

    DeleteInvestmentManagementListResponse deleteSingleData(DeleteInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    DeleteInvestmentManagementListResponse deleteMultipleApprove(DeleteInvestmentManagementListRequest requestList);

    String deleteAll();

    List<InvestmentManagementDTO> getAll();

    InvestmentManagementDTO getByCode(String investmentManagementCode);
}

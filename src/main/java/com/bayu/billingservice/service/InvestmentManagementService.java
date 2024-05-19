package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;

import java.util.List;

public interface InvestmentManagementService {

    boolean isCodeAlreadyExists(String code);

    InvestmentManagementResponse createSingleData(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse createMultipleData(CreateInvestmentManagementListRequest requestList, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse createSingleApprove(InvestmentManagementApproveRequest approveRequest);

    InvestmentManagementResponse updateSingleData(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse updateMultipleData(CreateInvestmentManagementListRequest requestList, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse updateSingleApprove(InvestmentManagementApproveRequest approveRequest);

    InvestmentManagementResponse deleteSingleData(DeleteInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse deleteSingleApprove(InvestmentManagementApproveRequest approveRequest);

    String deleteAll();

    List<InvestmentManagementDTO> getAll();

    InvestmentManagementDTO getByCode(String investmentManagementCode);
}

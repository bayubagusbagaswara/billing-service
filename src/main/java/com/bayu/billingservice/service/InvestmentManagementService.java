package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;

import java.util.List;

public interface InvestmentManagementService {

    boolean isExistsByCode(String code);

    boolean isCodeAlreadyExists(String code);

    InvestmentManagementResponse createSingleData(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse createMultipleData(CreateInvestmentManagementListRequest requestList, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse createSingleApprove(InvestmentManagementApproveRequest approveRequest, String approveIPAddress);

    InvestmentManagementResponse updateSingleData(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse updateMultipleData(UpdateInvestmentManagementListRequest requestList, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse updateSingleApprove(InvestmentManagementApproveRequest approveRequest, String approveIPAddress);

    InvestmentManagementResponse deleteSingleData(DeleteInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO);
    InvestmentManagementResponse deleteSingleApprove(InvestmentManagementApproveRequest approveRequest, String approveIPAddress);

    String deleteAll();

    List<InvestmentManagementDTO> getAll();

    InvestmentManagementDTO getByCode(String investmentManagementCode);
}

package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.util.ConvertBigDecimalUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BillingFundMapper {

    public BillingFundDTO mapToDTO(BillingFund billingFund) {
        return BillingFundDTO.builder()
                .id(billingFund.getId())
                .createdAt(billingFund.getCreatedAt())
                .updatedAt(billingFund.getUpdatedAt())
                .billingStatus(billingFund.getBillingStatus().getStatus())
                .approvalStatus(billingFund.getApprovalStatus().getStatus())
                .customerCode(billingFund.getCustomerCode())
                .subCode(billingFund.getSubCode())
                .customerName(billingFund.getCustomerName())
                .month(billingFund.getMonth())
                .year(String.valueOf(billingFund.getYear()))
                .billingNumber(billingFund.getBillingNumber())
                .billingPeriod(billingFund.getBillingPeriod())
                .billingStatementDate(billingFund.getBillingStatementDate())
                .billingPaymentDueDate(billingFund.getBillingPaymentDueDate())
                .billingCategory(billingFund.getBillingCategory())
                .billingType(billingFund.getBillingType())
                .billingTemplate(billingFund.getBillingTemplate())
                .investmentManagementCode(billingFund.getInvestmentManagementCode())
                .investmentManagementName(billingFund.getInvestmentManagementName())
                .investmentManagementAddress1(billingFund.getInvestmentManagementAddress1())
                .investmentManagementAddress2(billingFund.getInvestmentManagementAddress2())
                .investmentManagementAddress3(billingFund.getInvestmentManagementAddress3())
                .investmentManagementAddress4(billingFund.getInvestmentManagementAddress4())
                .investmentManagementEmail(billingFund.getInvestmentManagementEmail())
                .investmentManagementUniqueKey(billingFund.getInvestmentManagementUniqueKey())
                .account(billingFund.getAccount())
                .accountName(billingFund.getAccountName())
                .currency(billingFund.getCurrency())
                .accrualCustodialValueFrequency(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getAccrualCustodialValueFrequency()))
                .accrualCustodialSafekeepingFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getAccrualCustodialSafekeepingFee()))
                .accrualCustodialFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getAccrualCustodialFee()))
                .bis4TransactionValueFrequency(String.valueOf(billingFund.getBis4TransactionValueFrequency()))
                .bis4TransactionFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getBis4TransactionFee()))
                .bis4TransactionAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getBis4TransactionAmountDue()))
                .subTotal(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getSubTotal()))
                .vatFee(billingFund.getVatFee().stripTrailingZeros().toPlainString())
                .vatAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getVatAmountDue()))
                .kseiTransactionValueFrequency(String.valueOf(billingFund.getKseiTransactionValueFrequency()))
                .kseiTransactionFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getKseiTransactionFee()))
                .kseiTransactionAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getKseiTransactionAmountDue()))
                .totalAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getTotalAmountDue()))
                .build();
    }

    public List<BillingFundDTO> mapToDTOList(List<BillingFund> billingFundList) {
        return billingFundList.stream()
                .map(this::mapToDTO)
                .toList();
    }

}

package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.BillingCustomerDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.model.BillingCustomer;
import com.bayu.billingservice.repository.BillingCustomerRepository;
import com.bayu.billingservice.service.BillingCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class BillingCustomerServiceImpl implements BillingCustomerService {

    private final BillingCustomerRepository billingCustomerRepository;

    public BillingCustomerServiceImpl(BillingCustomerRepository billingCustomerRepository) {
        this.billingCustomerRepository = billingCustomerRepository;
    }

    @Override
    public BillingCustomerDTO create(CreateKycRequest request) {
        log.info("Create Kyc : {}", request);

        BigDecimal minimumFee = request.getCustomerMinimumFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerMinimumFee());
        BigDecimal customerFee = request.getCustomerSafekeepingFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerSafekeepingFee());

        BillingCustomer billingCustomer = BillingCustomer.builder()
                .investmentManagementName(request.getInvestmentManagementName())
                .investmentManagementAddressBuilding(request.getInvestmentManagementAddressBuilding())
                .investmentManagementAddressStreet(request.getInvestmentManagementAddressStreet())
                .investmentManagementAddressCity(request.getInvestmentManagementAddressCity())
                .investmentManagementAddressProvince(request.getInvestmentManagementAddressProvince())
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .accountBank(request.getAccountBank())
                .customerCode(request.getAid())
                .kseiSafeCode(request.getKseiSafeCode())
                .customerMinimumFee(minimumFee)
                .customerSafekeepingFee(customerFee)

                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .billingTemplate(request.getBillingTemplate())
                .build();

        return mapToDTO(billingCustomerRepository.save(billingCustomer));
    }

    @Override
    public List<BillingCustomerDTO> getAll() {
        return mapToDTOList(billingCustomerRepository.findAll());
    }

    @Override
    public List<BillingCustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        List<BillingCustomer> billingCustomerList = billingCustomerRepository.findByBillingCategoryAndBillingType(billingCategory, billingType);
        return mapToDTOList(billingCustomerList);
    }

    @Override
    public String deleteAll() {
        try {
            billingCustomerRepository.deleteAll();
            return "Successfully deleted all Kyc Customer";
        } catch (Exception e) {
            log.error("Error when delete all Kyc Customer : " + e.getMessage());
            throw new ConnectionDatabaseException("Error when delete all Kyc Customer");
        }
    }

    private static BillingCustomerDTO mapToDTO(BillingCustomer billingCustomer) {
        return BillingCustomerDTO.builder()
                .id(billingCustomer.getId())
                .customerCode(billingCustomer.getCustomerCode())
                .investmentManagementName(billingCustomer.getInvestmentManagementName())
                .investmentManagementAddressBuilding(billingCustomer.getInvestmentManagementAddressBuilding())
                .investmentManagementAddressStreet(billingCustomer.getInvestmentManagementAddressStreet())
                .investmentManagementAddressCity(billingCustomer.getInvestmentManagementAddressCity())
                .investmentManagementAddressProvince(billingCustomer.getInvestmentManagementAddressProvince())
                .accountName(billingCustomer.getAccountName())
                .accountNumber(billingCustomer.getAccountNumber())
                .accountBank(billingCustomer.getAccountBank())
                .kseiSafeCode(billingCustomer.getKseiSafeCode())
                .customerMinimumFee(billingCustomer.getCustomerMinimumFee())
                .customerSafekeepingFee(billingCustomer.getCustomerSafekeepingFee())
                .billingCategory(billingCustomer.getBillingCategory())
                .billingType(billingCustomer.getBillingType())
                .billingTemplate(billingCustomer.getBillingTemplate())
                .build();
    }

    private static List<BillingCustomerDTO> mapToDTOList(List<BillingCustomer> billingCustomerList) {
        return billingCustomerList.stream()
                .map(BillingCustomerServiceImpl::mapToDTO)
                .toList();
    }

}

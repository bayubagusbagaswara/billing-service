package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
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
    public KycCustomerDTO create(CreateKycRequest request) {
        log.info("Create Kyc : {}", request);

        BigDecimal minimumFee = request.getMinimumFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getMinimumFee());
        BigDecimal customerFee = request.getCustomerFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerFee());

        BillingCustomer billingCustomer = BillingCustomer.builder()
                .investmentManagementName(request.getInvestmentManagementName())
                .investmentManagementAddress(request.getInvestmentManagementAddress())
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
    public List<KycCustomerDTO> getAll() {
        return mapToDTOList(billingCustomerRepository.findAll());
    }

    @Override
    public List<KycCustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
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

    private static KycCustomerDTO mapToDTO(BillingCustomer billingCustomer) {
        return KycCustomerDTO.builder()
                .id(billingCustomer.getId())
                .aid(billingCustomer.getCustomerCode())
                .investmentManagementName(billingCustomer.getInvestmentManagementName())
                .investmentManagementAddress(billingCustomer.getInvestmentManagementAddress())
                .accountName(billingCustomer.getAccountName())
                .accountNumber(billingCustomer.getAccountNumber())
                .accountBank(billingCustomer.getAccountBank())
                .kseiSafeCode(billingCustomer.getKseiSafeCode())
                .minimumFee(billingCustomer.getCustomerMinimumFee())
                .customerFee(billingCustomer.getCustomerSafekeepingFee())
                .billingCategory(billingCustomer.getBillingCategory())
                .billingType(billingCustomer.getBillingType())
                .billingTemplate(billingCustomer.getBillingTemplate())
                .build();
    }

    private static List<KycCustomerDTO> mapToDTOList(List<BillingCustomer> billingCustomerList) {
        return billingCustomerList.stream()
                .map(BillingCustomerServiceImpl::mapToDTO)
                .toList();
    }

}

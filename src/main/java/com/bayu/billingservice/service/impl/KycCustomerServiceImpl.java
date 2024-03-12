package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.model.KycCustomer;
import com.bayu.billingservice.repository.KycCustomerRepository;
import com.bayu.billingservice.service.KycCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class KycCustomerServiceImpl implements KycCustomerService {

    private final KycCustomerRepository kycCustomerRepository;

    public KycCustomerServiceImpl(KycCustomerRepository kycCustomerRepository) {
        this.kycCustomerRepository = kycCustomerRepository;
    }

    @Override
    public KycCustomerDTO create(CreateKycRequest request) {
        log.info("Create Kyc : {}", request);

        BigDecimal minimumFee = request.getMinimumFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getMinimumFee());
        BigDecimal customerFee = request.getCustomerFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerFee());

        KycCustomer kycCustomer = KycCustomer.builder()
                .investmentManagementName(request.getInvestmentManagementName())
                .investmentManagementAddress(request.getInvestmentManagementAddress())
                .productName(request.getProductName())
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .accountBank(request.getAccountBank())
                .aid(request.getAid())
                .kseiSafeCode(request.getKseiSafeCode())
                .minimumFee(minimumFee)
                .customerFee(customerFee)
                .journal(request.getJournal())
                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .billingTemplate(request.getBillingTemplate())
                .build();

        return mapToDTO(kycCustomerRepository.save(kycCustomer));
    }

    @Override
    public List<KycCustomerDTO> getAll() {
        return mapToDTOList(kycCustomerRepository.findAll());
    }

    @Override
    public List<KycCustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        List<KycCustomer> kycCustomerList = kycCustomerRepository.findByBillingCategoryAndBillingType(billingCategory, billingType);
        return mapToDTOList(kycCustomerList);
    }

    @Override
    public String deleteAll() {
        try {
            kycCustomerRepository.deleteAll();
            return "Successfully deleted all Kyc Customer";
        } catch (Exception e) {
            log.error("Error when delete all Kyc Customer : " + e.getMessage());
            throw new ConnectionDatabaseException("Error when delete all Kyc Customer");
        }
    }

    private static KycCustomerDTO mapToDTO(KycCustomer kycCustomer) {
        return KycCustomerDTO.builder()
                .id(kycCustomer.getId())
                .aid(kycCustomer.getAid())
                .investmentManagementName(kycCustomer.getInvestmentManagementName())
                .investmentManagementAddress(kycCustomer.getInvestmentManagementAddress())
                .productName(kycCustomer.getProductName())
                .accountName(kycCustomer.getAccountName())
                .accountNumber(kycCustomer.getAccountNumber())
                .accountBank(kycCustomer.getAccountBank())
                .kseiSafeCode(kycCustomer.getKseiSafeCode())
                .minimumFee(kycCustomer.getMinimumFee())
                .customerFee(kycCustomer.getCustomerFee())
                .journal(kycCustomer.getJournal())
                .billingCategory(kycCustomer.getBillingCategory())
                .billingType(kycCustomer.getBillingType())
                .billingTemplate(kycCustomer.getBillingTemplate())
                .build();
    }

    private static List<KycCustomerDTO> mapToDTOList(List<KycCustomer> kycCustomerList) {
        return kycCustomerList.stream()
                .map(KycCustomerServiceImpl::mapToDTO)
                .toList();
    }

}

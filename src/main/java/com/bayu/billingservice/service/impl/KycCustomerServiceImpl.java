package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
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
        double customerFee = request.getCustomerFee().isEmpty() ? 0 : Double.parseDouble(request.getCustomerFee());

        KycCustomer kycCustomer = KycCustomer.builder()
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
    public KycCustomerDTO getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        KycCustomer kycCustomer = kycCustomerRepository.findByBillingCategoryAndBillingType(billingCategory, billingType)
                .orElseThrow(() -> new DataNotFoundException("Kyc not found with billing category : " + billingCategory + ", and billing type : " + billingType));
        return mapToDTO(kycCustomer);
    }

    private static KycCustomerDTO mapToDTO(KycCustomer kycCustomer) {
        return KycCustomerDTO.builder()
                .id(kycCustomer.getId())
                .aid(kycCustomer.getAid())
                .kseiSafeCode(kycCustomer.getKseiSafeCode())
                .minimumFee(kycCustomer.getMinimumFee())
                .customerFee(kycCustomer.getCustomerFee())
                .journal(kycCustomer.getJournal())
                .billingCategory(kycCustomer.getBillingCategory())
                .billingType(kycCustomer.getBillingType())
                .build();
    }

    private static List<KycCustomerDTO> mapToDTOList(List<KycCustomer> kycCustomerList) {
        return kycCustomerList.stream()
                .map(KycCustomerServiceImpl::mapToDTO)
                .toList();
    }

}

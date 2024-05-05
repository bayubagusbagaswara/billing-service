package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.customer.CreateCustomerListResponse;
import com.bayu.billingservice.dto.customer.CreateCustomerRequest;
import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.repository.CustomerRepository;
import com.bayu.billingservice.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return customerRepository.existsByCustomerCode(code);
    }

    @Override
    public CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public List<CustomerDTO> getAll() {
        return mapToDTOList(customerRepository.findAll());
    }

    @Override
    public List<CustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        List<Customer> customerList = customerRepository.findByBillingCategoryAndBillingType(billingCategory, billingType);
        return mapToDTOList(customerList);
    }

    @Override
    public String deleteAll() {
        try {
            customerRepository.deleteAll();
            return "Successfully deleted all Kyc Customer";
        } catch (Exception e) {
            log.error("Error when delete all Kyc Customer : {}", e.getMessage(), e);
            throw new ConnectionDatabaseException("Error when delete all Kyc Customer");
        }
    }

    private static CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .investmentManagementName(customer.getInvestmentManagementName())
                .accountName(customer.getAccountName())
                .accountNumber(customer.getAccountNumber())
                .accountBank(customer.getAccountBank())
                .kseiSafeCode(customer.getKseiSafeCode())
                .customerMinimumFee(customer.getCustomerMinimumFee())
                .customerSafekeepingFee(customer.getCustomerSafekeepingFee())
                .billingCategory(customer.getBillingCategory())
                .billingType(customer.getBillingType())
                .billingTemplate(customer.getBillingTemplate())
                .build();
    }

    private static List<CustomerDTO> mapToDTOList(List<Customer> customerList) {
        return customerList.stream()
                .map(CustomerServiceImpl::mapToDTO)
                .toList();
    }

}

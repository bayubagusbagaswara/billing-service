package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.customer.CreateCustomerRequest;
import com.bayu.billingservice.dto.customer.CustomerDTO;
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
    public CustomerDTO create(CreateCustomerRequest request) {
        log.info("Create Kyc : {}", request);

        BigDecimal minimumFee = request.getCustomerMinimumFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerMinimumFee());
        BigDecimal customerFee = request.getCustomerSafekeepingFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerSafekeepingFee());

        Customer customer = Customer.builder()
                .investmentManagementName(request.getInvestmentManagementName())
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .accountBank(request.getAccountBank())
                .customerCode(request.getCustomerCode())
                .kseiSafeCode(request.getKseiSafeCode())
                .customerMinimumFee(minimumFee)
                .customerSafekeepingFee(customerFee)

                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .billingTemplate(request.getBillingTemplate())
                .build();

        return mapToDTO(customerRepository.save(customer));
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
            log.error("Error when delete all Kyc Customer : " + e.getMessage());
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

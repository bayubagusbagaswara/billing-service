package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.customer.CreateCustomerRequest;
import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {

    private final ModelMapperUtil modelMapperUtil;

    public CustomerMapper(ModelMapperUtil modelMapperUtil) {
        this.modelMapperUtil = modelMapperUtil;
    }

    public Customer mapToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        modelMapperUtil.mapObjects(customerDTO, customer);
        return customer;
    }

    public CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        modelMapperUtil.mapObjects(customer, customerDTO);
        return customerDTO;
    }

    public List<CustomerDTO> mapToDTOList(List<Customer> customerList) {
        return customerList.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CustomerDTO mapFromCreateCustomerRequestToDTO(CreateCustomerRequest createCustomerRequest) {
        CustomerDTO customerDTO = new CustomerDTO();
        modelMapperUtil.mapObjects(createCustomerRequest, createCustomerRequest);
        return customerDTO;
    }

    public Customer createEntity(CustomerDTO customerDTO, BillingDataChangeDTO dataChangeDTO) {
        Customer customer = new Customer();
        modelMapperUtil.mapObjects(customerDTO, customer);
        customer.setApprovalStatus(dataChangeDTO.getApprovalStatus());
        customer.setInputId(dataChangeDTO.getInputId());
        customer.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        customer.setInputDate(dataChangeDTO.getInputDate());
        customer.setApprovalId(dataChangeDTO.getApproveId());
        customer.setApprovalIPAddress(dataChangeDTO.getApproveIPAddress());
        customer.setApprovalDate(dataChangeDTO.getApproveDate());
        return customer;
    }
}

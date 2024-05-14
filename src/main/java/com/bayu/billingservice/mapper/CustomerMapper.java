package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {
    private final ModelMapper modelMapper;
    private final ConvertDateUtil convertDateUtil;

    public CustomerMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        this.modelMapper = modelMapper;
        this.convertDateUtil = convertDateUtil;
        configureMapper();
    }

    private void configureMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.addMappings(new PropertyMap<Customer, CustomerDTO>() {
            @Override
            protected void configure() {
                skip(destination.getApprovalStatus());
                skip(destination.getInputId());
                skip(destination.getInputIPAddress());
                skip(destination.getInputDate());
                skip(destination.getApproveId());
                skip(destination.getApproveIPAddress());
                skip(destination.getApproveDate());
            }
        });
    }

    public Customer mapFromDtoToEntity(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }

    public CustomerDTO mapFromEntityToDto(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    public List<CustomerDTO> mapToDTOList(List<Customer> customerList) {
        return customerList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public CustomerDTO mapFromCreateCustomerRequestToDTO(CreateCustomerRequest createCustomerRequest) {
        return modelMapper.map(createCustomerRequest, CustomerDTO.class);
    }

    public Customer createEntity(CustomerDTO customerDTO, BillingDataChangeDTO dataChangeDTO) {
        Customer customer = modelMapper.map(customerDTO, Customer.class);
        setCommonProperties(customer, dataChangeDTO);
        return customer;
    }

    public Customer updateEntity(Customer customerUpdated, BillingDataChangeDTO dataChangeDTO) {
        Customer customer = modelMapper.map(customerUpdated, Customer.class);
        setCommonProperties(customer, dataChangeDTO);
        return customer;
    }

    private void setCommonProperties(Customer customer, BillingDataChangeDTO dataChangeDTO) {
        customer.setApprovalStatus(ApprovalStatus.APPROVED);
        customer.setInputId(dataChangeDTO.getInputId());
        customer.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        customer.setInputDate(dataChangeDTO.getInputDate());
        customer.setApproveId(dataChangeDTO.getApproveId());
        customer.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        customer.setApproveDate(convertDateUtil.getDate());
    }

    public CustomerDTO mapFromUpdateRequestToDto(UpdateCustomerRequest updateCustomerRequest) {
        return modelMapper.map(updateCustomerRequest, CustomerDTO.class);
    }

    public void mapObjects(CustomerDTO customerDTOSource, Customer customerTarget) {
        modelMapper.map(customerDTOSource, customerTarget);
    }
}

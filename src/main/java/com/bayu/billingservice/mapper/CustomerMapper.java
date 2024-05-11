package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.customer.CreateCustomerRequest;
import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.dto.customer.UpdateCustomerListRequest;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    private final ModelMapperUtil modelMapperUtil;
    private final ConvertDateUtil convertDateUtil;

    public Customer mapFromDtoToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        modelMapperUtil.mapObjects(customerDTO, customer);
        return customer;
    }

    public CustomerDTO mapFromEntityToDto(Customer customer) {
        ModelMapper modelMapper = new ModelMapper();
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

        return modelMapper.map(customer, CustomerDTO.class);
    }

    public List<CustomerDTO> mapToDTOList(List<Customer> customerList) {
        return customerList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public CustomerDTO mapFromCreateCustomerRequestToDTO(CreateCustomerRequest createCustomerRequest) {
        CustomerDTO customerDTO = new CustomerDTO();
        modelMapperUtil.mapObjects(createCustomerRequest, customerDTO);
        return customerDTO;
    }

    public Customer createEntity(CustomerDTO customerDTO, BillingDataChangeDTO dataChangeDTO) {
        Customer customer = new Customer();
        modelMapperUtil.mapObjects(customerDTO, customer);
        customer.setApprovalStatus(ApprovalStatus.APPROVED);
        customer.setInputId(dataChangeDTO.getInputId());
        customer.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        customer.setInputDate(dataChangeDTO.getInputDate());
        customer.setApproveId(dataChangeDTO.getApproveId());
        customer.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        customer.setApproveDate(convertDateUtil.getDate());
        return customer;
    }

    public Customer updateEntity(Customer customerUpdated, BillingDataChangeDTO dataChangeDTO) {
        Customer customer = new Customer();
        modelMapperUtil.mapObjects(customerUpdated, customer);
        customer.setApprovalStatus(dataChangeDTO.getApprovalStatus());
        customer.setInputId(dataChangeDTO.getInputId());
        customer.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        customer.setInputDate(dataChangeDTO.getInputDate());
        customer.setApproveId(dataChangeDTO.getApproveId());
        customer.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        customer.setApproveDate(convertDateUtil.getDate());
        return customer;
    }

    public CustomerDTO mapFromUpdateRequestToDto(UpdateCustomerListRequest updateCustomerListRequest) {
        CustomerDTO customerDTO = new CustomerDTO();
        modelMapperUtil.mapObjects(updateCustomerListRequest, customerDTO);
        return customerDTO;
    }
}

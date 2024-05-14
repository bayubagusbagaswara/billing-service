package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.customer.CreateCustomerRequest;
import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.dto.customer.UpdateCustomerRequest;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper extends BaseMapper<Customer, CustomerDTO> {

    private final ConvertDateUtil convertDateUtil;

    public CustomerMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        super(modelMapper);
        this.convertDateUtil = convertDateUtil;
    }

    @Override
    protected PropertyMap<Customer, CustomerDTO> getPropertyMap() {
        return new PropertyMap<>() {
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
        };
    }

    @Override
    protected Class<Customer> getEntityClass() {
        return Customer.class;
    }

    @Override
    protected Class<CustomerDTO> getDtoClass() {
        return CustomerDTO.class;
    }

    @Override
    protected void setCommonProperties(Customer entity, BillingDataChangeDTO dataChangeDTO) {
        entity.setApprovalStatus(ApprovalStatus.APPROVED);
        entity.setInputId(dataChangeDTO.getInputId());
        entity.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        entity.setInputDate(dataChangeDTO.getInputDate());
        entity.setApproveId(dataChangeDTO.getApproveId());
        entity.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        entity.setApproveDate(convertDateUtil.getDate());
    }

}

package com.bayu.billingservice.util;

import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.model.Customer;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.Mapping;

import java.util.List;

@Component
public class CustomerMapper {

    private final ModelMapperUtil modelMapperUtil;

    public CustomerMapper(ModelMapperUtil modelMapperUtil) {
        this.modelMapperUtil = modelMapperUtil;
//        modelMapper.addMappings(customerToDTOMapping);
    }

//    private final PropertyMap<Customer, CustomerDTO> customerToDTOMapping = new PropertyMap<>() {
//        @Override
//        protected void configure() {
//            // Menentukan pemetaan antara field-field Customer dan CustomerDTO
//            map().setCustomerCode(source.getCustomerCode());
//            map().setCustomerName(source.getCustomerName());
//            map().setBillingCategory(source.getBillingCategory());
//            // Menambahkan anotasi @Mapping untuk pemetaan khusus (jika diperlukan)
//            map().setNpwpNumber(source.getNpwpNumber()); // Pemetaan langsung
//            map().setNpwpName(source.getNpwpName()); // Pemetaan langsung
//            map().setNpwpAddress(source.getNpwpAddress()); // Pemetaan langsung
//            // Contoh penggunaan @Mapping dengan konversi kustom (String to BigDecimal)
//            @Mapping(source = "customerMinimumFee", target = "customerMinimumFee", qualifiedByName = "stringToBigDecimal")
//                    map().setCustomerMinimumFee(source.getCustomerMinimumFee());
//        }
//    };


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

}

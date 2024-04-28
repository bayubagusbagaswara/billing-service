package com.bayu.billingservice.util;

import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementRequest;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class InvestmentManagementValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateInvestmentManagementRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InvestmentManagementDTO investmentManagementDTO = (InvestmentManagementDTO) target;

        // Validate 'name' field
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.empty", "Name cannot be empty");

        // Validate 'password' field
//        if (investmentManagementDTO.getPassword() != null && userDTO.getPassword().length() < 6) {
//            errors.rejectValue("password", "password.minlength", "Password must be at least 6 characters long");
//        }

        // Validate 'email' field
//        if (userDTO.getEmail() != null && !userDTO.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
//            errors.rejectValue("email", "email.invalid", "Invalid email format");
//        }
    }
}

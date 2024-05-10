package com.bayu.billingservice;

import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BillingServiceApplication {

	public static void main(String[] args) {
		 SpringApplication.run(BillingServiceApplication.class, args);

//		String jsonDataBefore = "{\"code\":\"A001\",\"name\":\"PT BNI Asset Management\",\"email\":\"bni@mail.com\",\"address1\":\"Centennial Tower, Lantai 19\",\"address2\":\"Jl. Jendral Gatot Subroto Kav. 24-25\",\"address3\":\"\",\"address4\":\"Jakarta Selatan\"}";
//
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		try {
//			// Memetakan string JSON ke objek InvestmentManagementDTO
//			InvestmentManagementDTO dto = objectMapper.readValue(jsonDataBefore, InvestmentManagementDTO.class);
//
//			// Menggunakan objek DTO yang sudah dipetakan
//			System.out.println("ID: " + dto.getId());
//			System.out.println("Code: " + dto.getCode());
//			System.out.println("Name: " + dto.getName());
//			System.out.println("Email: " + dto.getEmail());
//			System.out.println("Address 1: " + dto.getAddress1());
//			System.out.println("Address 2: " + dto.getAddress2());
//			System.out.println("Address 3: " + dto.getAddress3());
//			System.out.println("Address 4: " + dto.getAddress4());
//
//			// Lakukan validasi DTO jika diperlukan
//			// Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
//			// Set<ConstraintViolation<InvestmentManagementDTO>> violations = validator.validate(dto);
//			// for (ConstraintViolation<InvestmentManagementDTO> violation : violations) {
//			//     System.out.println(violation.getMessage());
//			// }
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}



}

package com.bayu.billingservice.service.impl;

public class InvestmentManagementNewService {

//    public CreateInvestmentManagementListResponse create(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
//        try {
//            InvestmentManagementDTO investmentManagementDTO = createInvestmentManagementDTO(request);
//            List<String> errorMessages = validateInvestmentManagementDTO(investmentManagementDTO);
//
//            if (!isCodeUnique(investmentManagementDTO.getCode())) {
//                errorMessages.add("Investment Management is already taken with code: " + investmentManagementDTO.getCode());
//            }
//
//            prepareDataChangeDTO(dataChangeDTO, request, investmentManagementDTO);
//
//            if (errorMessages.isEmpty()) {
//                dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
//                return new CreateInvestmentManagementListResponse(1, 0, Collections.emptyList());
//            } else {
//                return new CreateInvestmentManagementListResponse(0, 1, buildErrorMessageList(investmentManagementDTO.getCode(), errorMessages));
//            }
//        } catch (Exception e) {
//            handleException(e);
//            return new CreateInvestmentManagementListResponse(0, 1, Collections.singletonList(new ErrorMessageInvestmentManagementDTO(null, Collections.singletonList(e.getMessage()))));
//        }
//    }
//
//    private InvestmentManagementDTO createInvestmentManagementDTO(CreateInvestmentManagementRequest request) {
//        return InvestmentManagementDTO.builder()
//                .code(request.getCode())
//                .name(request.getName())
//                .email(request.getEmail())
//                .address1(request.getAddress1())
//                .address2(request.getAddress2())
//                .address3(request.getAddress3())
//                .address4(request.getAddress4())
//                .build();
//    }
//
//    private boolean isCodeUnique(String code) {
//        return !investmentManagementRepository.existsByCode(code);
//    }
//
//    private void prepareDataChangeDTO(BillingDataChangeDTO dataChangeDTO, CreateInvestmentManagementRequest request, InvestmentManagementDTO investmentManagementDTO) throws JsonProcessingException {
//        dataChangeDTO.setInputId(request.getInputId());
//        dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
//        dataChangeDTO.setJsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO));
//    }
//
//    private List<ErrorMessageInvestmentManagementDTO> buildErrorMessageList(String code, List<String> errorMessages) {
//        return Collections.singletonList(new ErrorMessageInvestmentManagementDTO(code, errorMessages));
//    }
//
//    private void handleException(Exception e) {
//        log.error("An unexpected error occurred: {}", e.getMessage(), e);
//    }
//
//    public CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest request, BillingDataChangeDTO dataChangeDTO) {
//        try {
//            int totalDataSuccess = 0;
//            int totalDataFailed = 0;
//            List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();
//
//            for (InvestmentManagementDTO investmentManagementDTO : request.getInvestmentManagementRequestList()) {
//                List<String> errorMessages = validateInvestmentManagementDTO(investmentManagementDTO);
//
//                if (!isCodeUnique(investmentManagementDTO.getCode())) {
//                    errorMessages.add("Investment Management is already taken with code: " + investmentManagementDTO.getCode());
//                }
//
//                prepareDataChangeDTO(dataChangeDTO, request, investmentManagementDTO);
//
//                if (errorMessages.isEmpty()) {
//                    dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
//                    totalDataSuccess++;
//                } else {
//                    totalDataFailed++;
//                    errorMessageList.add(new ErrorMessageInvestmentManagementDTO(investmentManagementDTO.getCode(), errorMessages));
//                }
//            }
//
//            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
//        } catch (Exception e) {
//            handleException(e);
//            return new CreateInvestmentManagementListResponse(0, request.getInvestmentManagementRequestList().size(), Collections.singletonList(new ErrorMessageInvestmentManagementDTO(null, Collections.singletonList(e.getMessage()))));
//        }
//    }
}

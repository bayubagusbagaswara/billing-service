package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.*;
import com.bayu.billingservice.repository.SellingAgentRepository;
import com.bayu.billingservice.service.SellingAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellingAgentServiceImpl implements SellingAgentService {

    private final SellingAgentRepository sellingAgentRepository;

    @Override
    public boolean isCodeAlreadyExists(String sellingAgentCode) {
        return sellingAgentRepository.existsByCode(sellingAgentCode);
    }

    @Override
    public SellingAgentDTO getBySellingAgentCode(String sellingAgentCode) {
        return null;
    }

    @Override
    public CreateSellingAgentListResponse createSingleData(CreateSellingAgentRequest createSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public CreateSellingAgentListResponse createMultipleApprove(CreateSellingAgentListRequest createSellingAgentListRequest) {
        return null;
    }

    @Override
    public UpdateSellingAgentListResponse updateSingleData(UpdateSellingAgentRequest updateSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateSellingAgentListResponse updateMultipleData(UpdateSellingAgentListRequest updateSellingAgentListRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateSellingAgentListResponse updateMultipleApprove(UpdateSellingAgentListRequest updateSellingAgentListRequest) {
        return null;
    }

    @Override
    public DeleteSellingAgentListResponse deleteSingleData(DeleteSellingAgentRequest deleteSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public DeleteSellingAgentListResponse deleteMultipleApprove(DeleteSellingAgentListRequest deleteSellingAgentListRequest) {
        return null;
    }

    @Override
    public String deleteAll() {
        return "";
    }

    @Override
    public List<SellingAgentDTO> getAll() {
        return List.of();
    }
}

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
    public SellingAgentResponse createSingleData(CreateSellingAgentRequest createSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public SellingAgentResponse createSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest) {
        return null;
    }

    @Override
    public SellingAgentResponse updateSingleData(UpdateSellingAgentRequest updateSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public SellingAgentResponse updateMultipleData(SellingAgentListRequest listRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public SellingAgentResponse updateSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest) {
        return null;
    }

    @Override
    public SellingAgentResponse deleteSingleData(DeleteSellingAgentRequest deleteSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public SellingAgentResponse deleteSingleApprove(SellingAgentApproveRequest approveRequest) {
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

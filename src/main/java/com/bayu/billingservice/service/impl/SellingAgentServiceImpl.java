package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.sellingagent.SellingAgentDTO;
import com.bayu.billingservice.model.SellingAgent;
import com.bayu.billingservice.repository.SellingAgentRepository;
import com.bayu.billingservice.service.SellingAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private SellingAgentDTO mapToDTO(SellingAgent sellingAgent) {
        return SellingAgentDTO.builder()
                .id(sellingAgent.getId())
                .code(sellingAgent.getCode())
                .name(sellingAgent.getName())
                .build();
    }
}

package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.core.Core2DTO;
import com.bayu.billingservice.service.Core2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class Core2ServiceImpl implements Core2Service {


    @Override
    public List<Core2DTO> calculate(String category, String type, String monthYear) {
        return null;
    }

    @Override
    public String calculate1(String category, String type, String monthYear) {
        return null;
    }
}

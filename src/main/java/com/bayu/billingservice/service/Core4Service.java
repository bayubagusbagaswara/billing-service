package com.bayu.billingservice.service;

import java.util.List;
import java.util.Map;

public interface Core4Service {

    Map<String, List<Object>> calculate(String category, String type, String monthYear);
}

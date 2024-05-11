package com.bayu.billingservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static String cleanedJsonData(String jsonDataFull) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonDataFull);

        ((ObjectNode) jsonNode).remove("dataChangeId");
        ((ObjectNode) jsonNode).remove("approvalStatus");
        ((ObjectNode) jsonNode).remove("inputId");
        ((ObjectNode) jsonNode).remove("inputIPAddress");
        ((ObjectNode) jsonNode).remove("inputDate");
        ((ObjectNode) jsonNode).remove("approveId");
        ((ObjectNode) jsonNode).remove("approveIPAddress");
        ((ObjectNode) jsonNode).remove("approveDate");

        return objectMapper.writeValueAsString(jsonNode);
    }
}

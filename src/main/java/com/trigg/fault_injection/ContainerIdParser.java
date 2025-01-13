package com.trigg.fault_injection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContainerIdParser {

    public List<String> parseId(String jsonResponse) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        List<String> containerIds = new ArrayList<>();
        for (JsonNode container : rootNode) {
            String id = container.get("Id").asText(); // Get the "Id" field
            containerIds.add(id);
        }
        return containerIds;
    }
}

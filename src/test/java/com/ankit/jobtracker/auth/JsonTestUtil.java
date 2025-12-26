package com.ankit.jobtracker.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTestUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String extract(String json, String field) throws Exception {
        JsonNode node = mapper.readTree(json);
        return node.get(field).asText();
    }
}
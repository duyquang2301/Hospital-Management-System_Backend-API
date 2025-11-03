package com.wannabe.app.main.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper objectMapper = JsonMapper.builder()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build();

    public static String marshal(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T unmarshal(String jsonText, Class<T> type) throws JsonMappingException, JsonProcessingException {
        return objectMapper.readValue(jsonText, type);
    }

    public static <T> T mapToObject(Map<String, Object> data, Class<T> type) {
        return objectMapper.convertValue(data, type);
    }
}

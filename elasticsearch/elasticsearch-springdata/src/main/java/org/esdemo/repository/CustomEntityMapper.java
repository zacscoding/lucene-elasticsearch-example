package org.esdemo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomEntityMapper implements EntityMapper {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public String mapToString(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

    @Override
    public <T> T mapToObject(String s, Class<T> aClass) throws IOException {
        return objectMapper.readValue(s, aClass);
    }
}

package com.pillchill.migration.mapper;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class DataMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private DataMapper() {
    }

    public static <S, T> T map(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return MAPPER.convertValue(source, targetClass);
    }

    public static <S, T> List<T> mapList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream().map(item -> map(item, targetClass)).toList();
    }
}

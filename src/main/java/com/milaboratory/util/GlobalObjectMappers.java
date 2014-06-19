package com.milaboratory.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by dbolotin on 24.01.14.
 */
public class GlobalObjectMappers {
    public static final ObjectMapper ONE_LINE = new ObjectMapper();
    public static final ObjectMapper PRETTY = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static String toOneLine(Object object) throws JsonProcessingException {
        String str = GlobalObjectMappers.ONE_LINE.writeValueAsString(object);

        if (str.contains("\n"))
            throw new RuntimeException("Internal error.");

        return str;
    }
}

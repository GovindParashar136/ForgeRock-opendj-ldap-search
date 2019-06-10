package org.gjp.ldap.util;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JSONUtil {

	private JSONUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_EMPTY);

	public static <T> Object fromJSON(String jsonString, Class<T> clazz) throws IOException {
		OBJECT_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return OBJECT_MAPPER.readValue(jsonString, clazz);
	}

	public static <T> Object fromObject(Object object, Class<T> clazz) throws IOException {
		return fromJSON(createJSON(object), clazz);
	}

	public static String createJSON(Object object) throws JsonProcessingException {
		OBJECT_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return OBJECT_MAPPER.writeValueAsString(object);
	}
	
	
}

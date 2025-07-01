package com.monarchsolutions.sms.repository;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.Timestamp;

public class MapperUtil {

	/**
	 * Map each column in order: config[0] ← data[0], config[1] ← data[1], …
	 */

	public static <T> T mapRow(Object[] data, MappingConfig[] config, Class<T> clazz) {
		try {
			T d = clazz.getDeclaredConstructor().newInstance();

			for (int i = 0; i < config.length; i++) {
				MappingConfig mc = config[i];
				Object value = data[i];
				// skip nulls entirely
				if (value == null) continue;

				String methodName = "set" 
					+ Character.toUpperCase(mc.getField().charAt(0)) 
					+ mc.getField().substring(1);
				Method setter = clazz.getMethod(methodName, mc.getType());

				Class<?> type = mc.getType();
				if (Long.class.equals(type)) {
					setter.invoke(d, ((Number) value).longValue());
				}
				else if (Integer.class.equals(type)) {
					setter.invoke(d, ((Number) value).intValue());
				}
				else if (BigDecimal.class.equals(type)) {
					setter.invoke(d, (BigDecimal) value);
				}
				else if (String.class.equals(type)) {
					setter.invoke(d, value.toString());
				}
				else if (LocalDateTime.class.equals(type)) {
					if (value instanceof Timestamp) {
						setter.invoke(d, ((Timestamp) value).toLocalDateTime());
					} else {
						setter.invoke(d, value);
					}
				}
				else if (Boolean.class.equals(type)) {
					Boolean boolVal = null;
					// handle both Boolean and numeric (1 = true)
					if (value instanceof Boolean) {
						boolVal = (Boolean) value;
					} else if (value instanceof Number) {
						boolVal = ((Number) value).intValue() == 1;
					}
					setter.invoke(d, boolVal);
				}
				// … add other types here as needed …
			}

			return d;
		} catch (Exception e) {
			throw new RuntimeException("Error mapping row: " + e.getMessage(), e);
		}
	}
}

package blah.tests;

import java.lang.reflect.ParameterizedType;

import lombok.val;

public class Commons {

	public static Class<?> extractGenericTypeFromFirstInterface( Class<?> clazz ) {
		val pType = clazz.getGenericInterfaces();
		Class<?> type = (Class<?>)( (ParameterizedType)pType[0] ).getActualTypeArguments()[0];
		return type;
	}
}

package jp.hishidama.eclipse_plugin.toad.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataFileLoader {

	public static Map<String, String> load(String className) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Map<String, String> map = new LinkedHashMap<String, String>();

		Class<?> clazz = Class.forName(className);
		Object target = clazz.newInstance();
		for (Method m : clazz.getMethods()) {
			if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
				Object value = callGetter(target, m);
				map.put(m.getName(), toString(value));
			}
		}

		return map;
	}

	private static Object callGetter(Object target, Method method) {
		try {
			return method.invoke(target);
		} catch (IllegalAccessException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

	private static String toString(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		if (value instanceof Class) {
			return ((Class<?>) value).getName();
		}
		if (value instanceof List) {
			return toString((List<?>) value);
		}
		return String.valueOf(value);
	}

	private static String toString(List<?> list) {
		StringBuilder sb = new StringBuilder(64);
		for (Object obj : list) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(obj);
		}
		return sb.toString();
	}
}

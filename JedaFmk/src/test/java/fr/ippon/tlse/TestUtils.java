package fr.ippon.tlse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum TestUtils {
	SINGLETON;

	public Object invokePrivateMethode(String methodName, Object target, Object... parameters) {
		Method method;
		Object result = null;
		int paramCount = parameters.length;
		Class<?>[] classArray = new Class<?>[paramCount];
		for (int i = 0; i < paramCount; i++) {
			parameters[i] = parameters[i];
			classArray[i] = parameters[i].getClass();
		}
		try {
			method = target.getClass().getDeclaredMethod(methodName, classArray);
			method.setAccessible(true);
			result = method.invoke(target, parameters);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException("Impossible to invoke target private methode see cause for more detail", e);
		}
		return result;
	}
}

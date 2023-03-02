package be.masqueou.spigotmc.utils.reflection;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

	public static Class<?> getNmsClass(String name) {
		Class<?> result;
		String className = "net.minecraft.server." + getVersion() + "." + name;
		String newVersionClassName = "net.minecraft.server.MinecraftServer";
		
		if(getClass(className) == null) {
			result = getClass(newVersionClassName);
		} else {
			result = getClass(className);
		}
		
		return result;
	}

	public static String getVersion() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}



	public static Method makeMethod(Class<?> clazz, String methodName, Class<?>... paramaters) {
		try {
			return clazz.getDeclaredMethod(methodName, paramaters);
		} catch (NoSuchMethodException ex) {
			return null;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T callMethod(Method method, Object instance, Object... paramaters) {
		if (method == null)
			throw new RuntimeException("No such method");
		method.setAccessible(true);
		try {
			return (T) method.invoke(instance, paramaters);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex.getCause());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	public static Field makeField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException ex) {
			return null;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("unchecked")

	public static <T> T getField(Field field, Object instance) {
		if (field == null)
			throw new RuntimeException("No such field");
		field.setAccessible(true);
		try {
			return (T) field.get(instance);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException ex) {
			return null;
		}
	}

}

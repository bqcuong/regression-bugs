package com.gdssecurity.pmd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class TypeUtils {
	
	public static final TypeUtils INSTANCE = new TypeUtils();
	
	
	private Map<String, Class<?>> hits = new HashMap<>();
	private Set<String> misses = new HashSet<String>();
	
	private TypeUtils() {
		super();
	}
	
	
	
	
	/**
	 * Finds a class. Uses cache to store misses and hits
	 * @param className class name to search for
	 * @return
	 */
	public Class<?> getClassForName(String className) {
		if (StringUtils.isBlank(className)) {
			return null;
		}
		if (misses.contains(className)) {
			return null;
		}
		Class<?> cached = hits.get(className);
		if (cached != null) {
			return cached;
		}

		try {
			Class<?> clazz = Class.forName(className, false, this.getClass()
					.getClassLoader());
			hits.put(className, clazz);
			return clazz;

		} catch (NoClassDefFoundError | ExceptionInInitializerError	| ClassNotFoundException err) { 
			misses.add(className);
			return null;
		}		
	}

}

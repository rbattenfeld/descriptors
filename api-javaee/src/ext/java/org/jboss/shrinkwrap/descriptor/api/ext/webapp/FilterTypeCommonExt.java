package org.jboss.shrinkwrap.descriptor.api.ext.webapp;

import org.jboss.shrinkwrap.descriptor.api.FactoryExclude;

@FactoryExclude
public interface FilterTypeCommonExt<ORIGIN> {
		
	public ORIGIN filter(String name, String clazz, String[] urlPatterns);
	
	public ORIGIN filter(String name, Class<?> clazz, String[] urlPatterns);
	
}

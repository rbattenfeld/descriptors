package org.jboss.shrinkwrap.descriptor.impl.ext.webapp25;

import org.jboss.shrinkwrap.descriptor.api.ext.webapp25.FilterTypeExt;
import org.jboss.shrinkwrap.descriptor.api.webapp25.FilterType;
import org.jboss.shrinkwrap.descriptor.impl.webapp25.FilterMappingTypeImpl;
import org.jboss.shrinkwrap.descriptor.impl.webapp25.FilterTypeImpl;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;


public class FilterTypeExtImpl extends FilterTypeImpl implements FilterTypeExt {
	
	private FilterMappingTypeImpl mapping = null;

	@Override
	public void initialize(final String nodeName, final Node node) {
		super.initialize(nodeName, node);
		if (mapping != null) {
			mapping.initialize("filter-mapping", node);
		}
    }
	
	@Override
	public FilterType filter(String name, String clazz, String[] urlPatterns) {
		filterName(name);
		filterClass(clazz);
		mapping = new FilterMappingTypeImpl();
		mapping.filterName(name);
		mapping.urlPattern(urlPatterns);
		return this;
	}

	@Override
	public FilterType filter(String name, Class<?> clazz, String[] urlPatterns) {
		filterName(name);
		filterClass(clazz);
		mapping = new FilterMappingTypeImpl();
		mapping.filterName(name);
		mapping.urlPattern(urlPatterns);
		return this;
	}
}

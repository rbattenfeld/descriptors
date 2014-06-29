package org.jboss.shrinkwrap.descriptor.impl.ext.webcommon30;

import org.jboss.shrinkwrap.descriptor.api.ext.webcommon30.FilterTypeExt;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.FilterType;
import org.jboss.shrinkwrap.descriptor.impl.webcommon30.FilterMappingTypeImpl;
import org.jboss.shrinkwrap.descriptor.impl.webcommon30.FilterTypeImpl;
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

package org.jboss.shrinkwrap.descriptor.impl.base;

import org.jboss.shrinkwrap.descriptor.spi.node.Node;

public interface ChildNodeInitializer {
	
	public void initialize(String nodeName, Node node);

	public void assign(String nodeName, Node node);
}

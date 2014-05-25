package org.jboss.shrinkwrap.descriptor.metadata;

import org.junit.Test;

public class MetadataFactoryBuilderTest {

//	@Ignore
	@Test
	public void test() throws Exception {
		new MetadataFactoryBuilder()
		.createFactory("/home/bfr/business/shrinkwrap/descriptors/api-javaee/src/main/java", "/home/bfr/business/shrinkwrap/descriptors/impl-javaee/src/main/java", "JavaEE");
	}

}

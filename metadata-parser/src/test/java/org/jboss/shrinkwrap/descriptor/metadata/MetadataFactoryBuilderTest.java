package org.jboss.shrinkwrap.descriptor.metadata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.shrinkwrap.descriptor.metadata.codegen.FactoryBuilder;
import org.junit.Test;

public class MetadataFactoryBuilderTest {

//	@Ignore
	@Test
	public void test() throws Exception {
		new FactoryBuilder()
		.createFactory("/home/bfr/business/shrinkwrap/descriptors/api-javaee/src/main/java", "/home/bfr/business/shrinkwrap/descriptors/impl-javaee/src/main/java", "JavaEE");
	}
	
	@Test
	public void testRegex() throws Exception {
		final String dddd = "applica44tion6666";
//		final String nnnn = dddd.("\\d+");
		final Pattern p = Pattern.compile("\\d+");
		final Matcher m = p.matcher(dddd); 
		while (m.find()) {
		    System.out.println(m.group());
		}
	}

}

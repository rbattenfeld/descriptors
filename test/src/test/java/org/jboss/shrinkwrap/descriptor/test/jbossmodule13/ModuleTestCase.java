/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.descriptor.test.jbossmodule13;

import java.io.BufferedReader;
import java.io.FileReader;

import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.jbossmodule13.ModuleAbsentDescriptor;
import org.jboss.shrinkwrap.descriptor.api.jbossmodule13.ModuleAliasDescriptor;
import org.jboss.shrinkwrap.descriptor.api.jbossmodule13.ModuleDescriptor;
import org.jboss.shrinkwrap.descriptor.api.jbossmodule13.WrapperModuleDescriptor;
import org.jboss.shrinkwrap.descriptor.test.util.XmlAssert;
import org.junit.Test;

/**
 * Test Case to verify that the {@link org.jboss.shrinkwrap.descriptor.api.jbossmodule13.ModuleDescriptor}
 * impl produces the correct XML Descriptor output.
 *
 * @author <a href="mailto:toby@tcrawley.org">Toby Crawley</a>
 */

public class ModuleTestCase {

    @Test
    public void testGeneratedXmlForModule() throws Exception {
        final ModuleDescriptor module = Descriptors.create(ModuleDescriptor.class)
                .addDefaultNamespaces()
                .name("some.module")
                .slot("impl")
                .getOrCreateResources()
                .getOrCreateArtifact()
                .name("group:artifact:1.1.0").up().up()
                .getOrCreateDependencies()
                .getOrCreateSystem()
                .export(true)
                .getOrCreatePaths()
                .createPath()
                .name("foo/bar/baz").up().up().up()
                .createModule()
                .name("a.module")
                .slot("impl")
                .services("import")
                .export(true).up().up();

        final String moduleGenerated = module.exportAsString();
        final String moduleExpected = getResourceContents("src/test/resources/test-module.xml");
        XmlAssert.assertIdentical(moduleExpected, moduleGenerated);
    }

    @Test
    public void testGeneratedXmlForModuleAlias() throws Exception {
        final ModuleAliasDescriptor module = Descriptors.create(ModuleAliasDescriptor.class)
                .addDefaultNamespaces()
                .name("some.module")
                .slot("impl")
                .targetName("some.other.module")
                .targetSlot("main");

        final String moduleGenerated = module.exportAsString();
        final String moduleExpected = getResourceContents("src/test/resources/test-alias-module.xml");
        XmlAssert.assertIdentical(moduleExpected, moduleGenerated);
    }

    @Test
    public void testGeneratedXmlForModuleAbsent() throws Exception {
        final ModuleAbsentDescriptor module = Descriptors.create(ModuleAbsentDescriptor.class)
                .addDefaultNamespaces()
                .name("some.module")
                .slot("impl");

        final String moduleGenerated = module.exportAsString();
        final String moduleExpected = getResourceContents("src/test/resources/test-absent-module.xml");
        XmlAssert.assertIdentical(moduleExpected, moduleGenerated);
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper ---------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private String getResourceContents(String resource) throws Exception {
        assert resource != null && resource.length() > 0 : "Resource must be specified";
        final BufferedReader reader = new BufferedReader(new FileReader(resource));
        final StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        return builder.toString();
    }
        
    public class WrapperModuleDescriptorImpl implements WrapperModuleDescriptor {
    	private final WrapperModuleDescriptor descriptor;
    	
    	private WrapperModuleDescriptorImpl(final WrapperModuleDescriptor descriptor) {
    		this.descriptor = descriptor;
    	}
    	
    	public static WrapperModuleDescriptor fromString(final String fileContent) {
    		if (fileContent.indexOf("<module-absent") >= 0) {
    			return new WrapperModuleDescriptorImpl((WrapperModuleDescriptor) Descriptors.importAs(ModuleAbsentDescriptor.class).fromString(fileContent));
    		} else if (fileContent.indexOf("<module-alias") >= 0) {
    			return new WrapperModuleDescriptorImpl((WrapperModuleDescriptor) Descriptors.importAs(ModuleAliasDescriptor.class).fromString(fileContent));
    		} else if (fileContent.indexOf("<module") >= 0) {
    			return new WrapperModuleDescriptorImpl((WrapperModuleDescriptor) Descriptors.importAs(ModuleDescriptor.class).fromString(fileContent));
    		} else {
    			throw new IllegalArgumentException("Wrong content");
    		}
    	}

		@Override
		public WrapperModuleDescriptor slot(String slot) {
			return descriptor.slot(slot);
		}

		@Override
		public String getSlot() {
			return descriptor.getSlot();
		}

		@Override
		public WrapperModuleDescriptor removeSlot() {
			return descriptor.removeSlot();
		}

		@Override
		public WrapperModuleDescriptor name(String name) {
			return descriptor.name(name);
		}

		@Override
		public String getName() {
			return descriptor.getName();
		}

		@Override
		public WrapperModuleDescriptor removeName() {
			return descriptor.removeName();
		}
    	
    	
    }

}

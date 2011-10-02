/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.descriptor.impl.resourceadapters10; 

import org.jboss.shrinkwrap.descriptor.spi.node.Node;
import org.jboss.shrinkwrap.descriptor.gen.TestDescriptorImpl;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;import org.junit.Test;
import static org.junit.Assert.*;
import org.jboss.shrinkwrap.descriptor.api.resourceadapters10.ResourceAdapterType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jboss.shrinkwrap.descriptor.api.Child;
import org.jboss.shrinkwrap.descriptor.impl.base.XMLDate;
import org.jboss.shrinkwrap.descriptor.impl.base.Strings;
import org.jboss.shrinkwrap.descriptor.api.DescriptorExporter;
import org.jboss.shrinkwrap.descriptor.api.resourceadapters10.BeanValidationGroupsType;
import org.jboss.shrinkwrap.descriptor.impl.resourceadapters10.BeanValidationGroupsTypeImpl;
import org.jboss.shrinkwrap.descriptor.api.resourceadapters10.ConfigPropertyType;
import org.jboss.shrinkwrap.descriptor.impl.resourceadapters10.ConfigPropertyTypeImpl;
import org.jboss.shrinkwrap.descriptor.api.resourceadapters10.TransactionSupportType;
import org.jboss.shrinkwrap.descriptor.api.resourceadapters10.ConnectionDefinitionsType;
import org.jboss.shrinkwrap.descriptor.impl.resourceadapters10.ConnectionDefinitionsTypeImpl;
import org.jboss.shrinkwrap.descriptor.api.resourceadapters10.AdminObjectsType;
import org.jboss.shrinkwrap.descriptor.impl.resourceadapters10.AdminObjectsTypeImpl;

public class ResourceAdapterTypeImplTestCase
{   
   @Test
   public void testNullArg() throws Exception
   {
      TestDescriptorImpl provider = new TestDescriptorImpl("test");
      ResourceAdapterType<TestDescriptorImpl> type = new ResourceAdapterTypeImpl<TestDescriptorImpl>(provider, "resource-adapterType", provider.getRootNode());
      TestDescriptorImpl.testNullArgs(type);
   }
   
   @Test
   public void testArchive() throws Exception
   {
      TestDescriptorImpl provider = new TestDescriptorImpl("test");
      ResourceAdapterType<TestDescriptorImpl> type = new ResourceAdapterTypeImpl<TestDescriptorImpl>(provider, "resource-adapterType", provider.getRootNode());
      type.archive("test");
      assertEquals(type.getArchive(), "test");
      type.removeArchive();
      assertNull(type.getArchive());
   }

   
   @Test
   public void testBeanValidationGroups() throws Exception
   {
      TestDescriptorImpl provider = new TestDescriptorImpl("test");
      ResourceAdapterType<TestDescriptorImpl> type = new ResourceAdapterTypeImpl<TestDescriptorImpl>(provider, "resource-adapterType", provider.getRootNode());
      type.getOrCreateBeanValidationGroups().up();
      type.removeBeanValidationGroups();
      assertNull(provider.getRootNode().getSingle("bean-validation-groups"));
   }

   
   @Test
   public void testBootstrapContext() throws Exception
   {
      TestDescriptorImpl provider = new TestDescriptorImpl("test");
      ResourceAdapterType<TestDescriptorImpl> type = new ResourceAdapterTypeImpl<TestDescriptorImpl>(provider, "resource-adapterType", provider.getRootNode());
      type.bootstrapContext("test");
      assertEquals(type.getBootstrapContext(), "test");
      type.removeBootstrapContext();
      assertNull(type.getBootstrapContext());
   }

   
   @Test
   public void testConfigProperty() throws Exception
   {
      TestDescriptorImpl provider = new TestDescriptorImpl("test");
      ResourceAdapterType<TestDescriptorImpl> type = new ResourceAdapterTypeImpl<TestDescriptorImpl>(provider, "resource-adapterType", provider.getRootNode());
      type.createConfigProperty().up();
      type.createConfigProperty().up();
      assertTrue(type.getAllConfigProperty().size() == 2);
      type.removeAllConfigProperty();
      assertTrue(type.getAllConfigProperty().size() == 0);
   }

   
   @Test
   public void testTransactionSupport() throws Exception
   {
      TestDescriptorImpl provider = new TestDescriptorImpl("test");
      ResourceAdapterType<TestDescriptorImpl> type = new ResourceAdapterTypeImpl<TestDescriptorImpl>(provider, "resource-adapterType", provider.getRootNode());
      for(TransactionSupportType e: TransactionSupportType.values())
      {
         type.transactionSupport(e);
         assertEquals(type.getTransactionSupport(), e);
         type.transactionSupport(e.toString());
         assertEquals(type.getTransactionSupportAsString(), e.toString());
      }
   }

   
   @Test
   public void testConnectionDefinitions() throws Exception
   {
      TestDescriptorImpl provider = new TestDescriptorImpl("test");
      ResourceAdapterType<TestDescriptorImpl> type = new ResourceAdapterTypeImpl<TestDescriptorImpl>(provider, "resource-adapterType", provider.getRootNode());
      type.getOrCreateConnectionDefinitions().up();
      type.removeConnectionDefinitions();
      assertNull(provider.getRootNode().getSingle("connection-definitions"));
   }

   
   @Test
   public void testAdminObjects() throws Exception
   {
      TestDescriptorImpl provider = new TestDescriptorImpl("test");
      ResourceAdapterType<TestDescriptorImpl> type = new ResourceAdapterTypeImpl<TestDescriptorImpl>(provider, "resource-adapterType", provider.getRootNode());
      type.getOrCreateAdminObjects().up();
      type.removeAdminObjects();
      assertNull(provider.getRootNode().getSingle("admin-objects"));
   }
}

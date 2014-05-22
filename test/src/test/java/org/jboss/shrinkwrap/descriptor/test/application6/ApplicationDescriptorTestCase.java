package org.jboss.shrinkwrap.descriptor.test.application6;

import java.io.BufferedReader;
import java.io.FileReader;

import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.application6.ApplicationDescriptor;
import org.jboss.shrinkwrap.descriptor.impl.FactoryImpl;
import org.jboss.shrinkwrap.descriptor.test.util.XmlAssert;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationDescriptorTestCase {

    @Test
    public void testImportGeneratedApplicationXml() throws Exception {
        ApplicationDescriptor app6Descr = create()
         .addDefaultNamespaces()
         .version("6")
         .applicationName("application-name0")
         .description("description0")
         .displayName("display-name0")
         .addIcon(FactoryImpl.instance().iconTypeJavaee6()
            .smallIcon("small-icon0")
            .largeIcon("large-icon0"))
         .initializeInOrder("true")
         .addModule(FactoryImpl.instance().moduleTypeApplication6()
            .ejb("ejb0")
            .altDd("alt-dd0"))
         .addSecurityRole(FactoryImpl.instance().securityRoleTypeJavaee6()
            .description("description1")
            .roleName("role-name0"))
         .libraryDirectory("library-directory0")
         .addEnvEntry(FactoryImpl.instance().envEntryTypeJavaee6()
            .description("description2")
            .envEntryName("env-entry-name0")
            .envEntryType("env-entry-type0")
            .envEntryValue("env-entry-value0")
            .mappedName("mapped-name0")
            .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
               .injectionTargetClass("injection-target-class0")
               .injectionTargetName("$"))
            .lookupName("lookup-name0"))
         .addEjbRef(FactoryImpl.instance().ejbRefTypeJavaee6()
            .description("description3")
            .ejbRefName("ejb-ref-name0")
            .ejbRefType("Entity")
            .home("home0")
            .remote("remote0")
            .ejbLink("ejb-link0")
            .mappedName("mapped-name1")
            .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
               .injectionTargetClass("injection-target-class1")
               .injectionTargetName("$"))
            .lookupName("lookup-name1"))
         .addEjbLocalRef(FactoryImpl.instance().ejbLocalRefTypeJavaee6()
            .description("description4")
            .ejbRefName("ejb-ref-name1")
            .ejbRefType("Entity")
            .localHome("local-home0")
            .local("local0")
            .ejbLink("ejb-link1")
            .mappedName("mapped-name2")
            .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
               .injectionTargetClass("injection-target-class2")
               .injectionTargetName("$"))
            .lookupName("lookup-name2"))
         .addServiceRef(FactoryImpl.instance().serviceRefTypeJavaeewebservicesclient13()
               .description("description5")
               .displayName("display-name1")
               .addIcon(FactoryImpl.instance().iconTypeJavaee6()
                  .smallIcon("small-icon1")
                  .largeIcon("large-icon1"))
               .serviceRefName("service-ref-name0")
               .serviceInterface("service-interface0")
               .serviceRefType("service-ref-type0")
               .wsdlFile("http://www.oxygenxml.com/")
               .jaxrpcMappingFile("jaxrpc-mapping-file0")
               .serviceQname("qName")
               .addPortComponentRef(FactoryImpl.instance().portComponentRefTypeJavaeewebservicesclient13()
                  .serviceEndpointInterface("service-endpoint-interface0")
                  .enableMtom(false)
                  .mtomThreshold(50)
                  .addressing(FactoryImpl.instance().addressingTypeJavaeewebservicesclient13()
                     .enabled(false)
                     .required(false)
                     .responses("ANONYMOUS"))
                  .respectBinding(FactoryImpl.instance().respectBindingTypeJavaeewebservicesclient13()
                     .enabled(false))
                  .portComponentLink("port-component-link0"))
               .addHandler(FactoryImpl.instance().handlerTypeJavaeewebservicesclient13()
                  .description("description6")
                  .displayName("display-name2")
                  .addIcon(FactoryImpl.instance().iconTypeJavaee6()
                     .smallIcon("small-icon2")
                     .largeIcon("large-icon2"))
                  .handlerName("handler-name0")
                  .handlerClass("handler-class0")
                  .addInitParam(FactoryImpl.instance().paramValueTypeJavaee6()
                     .description("description7")
                     .paramName("param-name0")
                     .paramValue("param-value0"))
                  .soapHeader("qName")
                  .soapRole("soap-role0")
                  .portName("port-name0"))
               .mappedName("mapped-name3")
               .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
                  .injectionTargetClass("injection-target-class3")
                  .injectionTargetName("$"))
               .lookupName("lookup-name3"))
         .addResourceRef(FactoryImpl.instance().resourceRefTypeJavaee6()
            .description("description8")
            .resRefName("res-ref-name0")
            .resType("res-type0")
            .resAuth("Application")
            .resSharingScope("Shareable")
            .mappedName("mapped-name4")
            .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
               .injectionTargetClass("injection-target-class4")
               .injectionTargetName("$"))
            .lookupName("lookup-name4"))
         .addResourceEnvRef(FactoryImpl.instance().resourceEnvRefTypeJavaee6()
            .description("description9")
            .resourceEnvRefName("resource-env-ref-name0")
            .resourceEnvRefType("resource-env-ref-type0")
            .mappedName("mapped-name5")
            .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
               .injectionTargetClass("injection-target-class5")
               .injectionTargetName("$"))
            .lookupName("lookup-name5"))
         .addMessageDestinationRef(FactoryImpl.instance().messageDestinationRefTypeJavaee6()
            .description("description10")
            .messageDestinationRefName("message-destination-ref-name0")
            .messageDestinationType("message-destination-type0")
            .messageDestinationUsage("Consumes")
            .messageDestinationLink("message-destination-link0")
            .mappedName("mapped-name6")
            .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
               .injectionTargetClass("injection-target-class6")
               .injectionTargetName("$"))
            .lookupName("lookup-name6"))
         .addPersistenceContextRef(FactoryImpl.instance().persistenceContextRefTypeJavaee6()
            .description("description11")
            .persistenceContextRefName("persistence-context-ref-name0")
            .persistenceUnitName("persistence-unit-name0")
            .persistenceContextType("Transaction")
            .addPersistenceProperty(FactoryImpl.instance().propertyTypeJavaee6()
               .name("name0")
               .value("value0"))
            .mappedName("mapped-name7")
            .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
               .injectionTargetClass("injection-target-class7")
               .injectionTargetName("$")))
          .addPersistenceUnitRef(FactoryImpl.instance().persistenceUnitRefTypeJavaee6()
             .description("description12")
             .persistenceUnitRefName("persistence-unit-ref-name0")
             .persistenceUnitName("persistence-unit-name1")
             .mappedName("mapped-name8")
             .addInjectionTarget(FactoryImpl.instance().injectionTargetTypeJavaee6()
               .injectionTargetClass("injection-target-class8")
               .injectionTargetName("$")))
          .addMessageDestination(FactoryImpl.instance().messageDestinationTypeJavaee6()
             .description("description13")
             .displayName("display-name3")
             .addIcon(FactoryImpl.instance().iconTypeJavaee6()
                .smallIcon("small-icon3")
                .largeIcon("large-icon3"))
             .messageDestinationName("message-destination-name0")
             .mappedName("mapped-name9")
             .lookupName("lookup-name7"))
          .addDataSource(FactoryImpl.instance().dataSourceTypeJavaee6()
             .description("description14")
             .name("name1")
             .className("class-name0")
             .serverName("server-name0")
             .portNumber(0)
             .databaseName("database-name0")
             .url("jdbc::")
             .user("user0")
             .password("password0")
             .addProperty(FactoryImpl.instance().propertyTypeJavaee6()
                .name("name2")
                .value("value1"))
             .loginTimeout(0)
             .transactional(false)
             .isolationLevel("TRANSACTION_READ_UNCOMMITTED")
             .initialPoolSize(0)
             .maxPoolSize(0)
             .minPoolSize(0)
             .maxIdleTime(0)
             .maxStatements(0));

        String appXmlOriginal = getResourceContents("src/test/resources/test-gen-application6.xml");
        String appXmlGenerated = app6Descr.exportAsString();

        XmlAssert.assertIdentical(appXmlOriginal, appXmlGenerated);
    }

    @Test
    public void testFixedAttribute() {
        final ApplicationDescriptor app6Descr = create();
        app6Descr.version(ApplicationDescriptor.VERSION);
        Assert.assertEquals(ApplicationDescriptor.VERSION, app6Descr.getVersion());
    }

    // -------------------------------------------------------------------------------------||
    // Helper Methods ----------------------------------------------------------------------||
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

    private ApplicationDescriptor create() {
        return Descriptors.create(ApplicationDescriptor.class);
    }

}

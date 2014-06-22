package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.util.Enumeration;
import java.util.List;

import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.DescriptorNamespace;
import org.jboss.shrinkwrap.descriptor.impl.base.ChildNodeInitializer;
import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataDescriptor;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataItem;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataJavaDoc;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataParserPath;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;
import org.jboss.shrinkwrap.descriptor.spi.node.NodeDescriptorImplBase;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class DescriptorBuilder {

    private static final String[] SEARCH_LIST = new String[] { "DESCRIPTOR_NAME", "_NS_KEY_1", "_NS_VALUE_1", "_NS_KEY_2", "_NS_VALUE_2", "_NS_KEY_3", "_NS_VALUE_3", "CONTEXT" };

    private static final String GET_FACTORY_METHOD = "\n"
        + "     /**\n"
        + "      * Returns the factory instance.\n"
        + "      * @return <code>Factory</code> \n"
        + "      */\n"
        + "     public org.jboss.shrinkwrap.descriptor.api.FactoryCONTEXT factory() {\n"
        + "         return new org.jboss.shrinkwrap.descriptor.impl.FactoryCONTEXTImpl();\n"
        + "     }\n";

    private static final String ADD_NAMESPACES = "\n"
        + "     /**\n"
        + "      * Adds the default namespaces as defined in the specification \n"
        + "      * @return the current instance of <code>DESCRIPTOR_NAME</code> \n"
        + "      */\n"
        + "     public DESCRIPTOR_NAME addDefaultNamespaces() {\n"
        + "         addNamespace(\"_NS_KEY_1\", \"_NS_VALUE_1\");\n"
        + "         addNamespace(\"_NS_KEY_2\", \"_NS_VALUE_2\");\n"
        + "         addNamespace(\"_NS_KEY_3\", \"_NS_VALUE_3\");\n"
        + "         return this;\n"
        + "     }\n";

    private static final String ADD_NAMESPACES_NO_DEFAULT = "\n"
        + "     /**\n"
        + "      * Adds the default namespaces as defined in the specification \n"
        + "      * @return the current instance of <code>DESCRIPTOR_NAME</code> \n"
        + "      */\n"
        + "     public DESCRIPTOR_NAME addDefaultNamespaces() {\n"
        + "         return this;\n"
        + "     }\n";


    private static final String ADD_NAMESPACE = "\n"
        + "     /**\n"
        + "      * Adds a new namespace\n"
        + "      * @return the current instance of <code>DESCRIPTOR_NAME</code> \n"
        + "      */\n"
        + "     public DESCRIPTOR_NAME addNamespace(String name, String value) {\n"
        + "         model.attribute(name, value);\n"
        + "         return this;\n"
        + "     }\n";

    private static final String GET_NAMESPACES = "\n"
        + "     /**\n"
        + "      * Returns all defined namespaces. \n"
        + "      * @return all defined namespaces \n"
        + "      */\n"
        + "     public java.util.List<String> getNamespaces() {\n"
        + "         java.util.List<String> namespaceList = new java.util.ArrayList<String>();\n"
        + "         java.util.Map<String, String> attributes = model.getAttributes();\n"
        + "         for (java.util.Map.Entry<String, String> e : attributes.entrySet()) {\n"
        + "             final String name = e.getKey();\n"
        + "             final String value = e.getValue();\n"
        + "             if (value != null && value.startsWith(\"http://\")) {\n"
        + "                 namespaceList.add(name + \"=\" + value);\n"
        + "             }\n"
        + "         }\n"
        + "         return namespaceList;\n"
        + "     }\n";

    private static final String REM_ALL_NAMESPACES = "\n"
        + "     /**\n"
        + "      * Removes all existing namespaces. \n"
        + "      * @return the current instance of <code>DESCRIPTOR_NAME</code> \n"
        + "      */\n"
        + "     public DESCRIPTOR_NAME removeAllNamespaces() {\n"
        + "         java.util.List<String> nameSpaceKeys = new java.util.ArrayList<String>();\n"
        + "         java.util.Map<String, String> attributes = model.getAttributes();\n"
        + "         for (java.util.Map.Entry<String, String> e : attributes.entrySet()) {\n"
        + "             final String name = e.getKey();\n"
        + "             final String value = e.getValue();\n"
        + "             if (value != null && value.startsWith(\"http://\")) {\n"
        + "                 nameSpaceKeys.add(name);\n"
        + "             }\n"
        + "         }\n"
        + "         for (String name: nameSpaceKeys) {\n"
        + "             model.removeAttribute(name);\n"
        + "         }\n"
        + "         return this;\n"
        + "     }\n";

    private static final String[] FACTORY_METHOD_LIST = new String[] {
        GET_FACTORY_METHOD,
    };

    private static final String[] NS_METHOD_LIST = new String[] {
        ADD_NAMESPACES,
        ADD_NAMESPACE,
        GET_NAMESPACES,
        REM_ALL_NAMESPACES,
    };

    private static final String[] NS_NO_DEFAULT_METHOD_LIST = new String[] {
        ADD_NAMESPACES_NO_DEFAULT,
        ADD_NAMESPACE,
        GET_NAMESPACES,
        REM_ALL_NAMESPACES,
    };

    public void generate(final Metadata metadata, final String pathToMetadata, final List<? extends MetadataJavaDoc> javadocTags, final MetadataParserPath path, final String factoryContext) throws Exception {
        for (Boolean isInterface : new Boolean[] { true, false }) {
            generateDescriptors(metadata, BuilderUtil.getPath(path, isInterface), isInterface, factoryContext);
        }
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private void generateDescriptors(final Metadata metadata, final String path, final boolean isApi, final String factoryContext) throws Exception {
        for (final MetadataDescriptor descriptor : metadata.getMetadataDescriptorList()) {
            if (descriptor.getRootElementName() != null && descriptor.getRootElementType() != null) {
                final String className = BuilderUtil.getClassName(descriptor, isApi) + "Tmp";
                final String fullyQualifiedFactoryName = BuilderUtil.getPackage(descriptor, isApi) + "." + className;
                final JCodeModel jcm = new JCodeModel();
                final JDefinedClass clazz = BuilderUtil.getClass(jcm, fullyQualifiedFactoryName, isApi);
                if (isApi) {
                    clazz._extends(Descriptor.class);
                    clazz._extends(jcm.ref(DescriptorNamespace.class).narrow(clazz));
                } else {
                    final String fqnApi = BuilderUtil.getPackage(descriptor, true) + "." + BuilderUtil.getClassName(descriptor, true) + "Tmp";
                    final JClass apiClass = jcm.directClass(fqnApi);
                    clazz._extends(NodeDescriptorImplBase.class);
                    clazz._implements(jcm.ref(DescriptorNamespace.class).narrow(apiClass));
                    clazz._implements(apiClass);
                    clazz.field(JMod.PRIVATE, Node.class, "model");
                    clazz.field(JMod.PRIVATE, ChildNodeInitializer.class, "dummy");
    //                clazz.field(JMod.PRIVATE, Node.class, "detachedNode");
    //                clazz.field(JMod.PRIVATE, boolean.class, "isDetached", JExpr.TRUE);
                    addConstructors(clazz);
                    addGetRootNode(clazz);
    //                addChildNodeInitializerMethods(clazz);
                }
                final MetadataItem rootElement = BuilderUtil.findClass(metadata, descriptor.getRootElementType());
                if (rootElement != null) {
                    if (factoryContext != null && !factoryContext.isEmpty()) {
                        for (String methodBody : FACTORY_METHOD_LIST) {
                            clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, getReplaceList(className, descriptor, factoryContext)));
                        }
                    }
                    for (String methodBody : getNamespaceMethods(descriptor)) {
                        clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, getReplaceList(className, descriptor, factoryContext)));
                    }
                    for (final MetadataElement element : BuilderUtil.getElementList(rootElement, metadata)) {
                        clazz.direct(element.asClassComment());
                        for (MethodGeneratorContract methodGenerator : BuilderUtil.getMethodGenerators()) {
                            if (methodGenerator.addMethods(clazz, metadata, element, className, isApi)) {
                                break;
                            }
                        }
                    }
                    final File file = new File(path);
                    jcm.build(file);
                }
            }
        }
    }

    private void addConstructors(final JDefinedClass clazz) {
        final JMethod createChildConstr = clazz.constructor(JMod.PUBLIC);
        createChildConstr.param(JMod.FINAL, String.class, "descriptorName");
        createChildConstr.body().directStatement("this(descriptorName, new Node(\"application\"));");

        final JMethod assignChildConstr = clazz.constructor(JMod.PUBLIC);
        assignChildConstr.param(JMod.FINAL, String.class, "descriptorName");
        assignChildConstr.param(JMod.FINAL, Node.class, "node");
        assignChildConstr.body().directStatement("super(descriptorName);this.model = node;addDefaultNamespaces();");
    }

    private void addGetRootNode(final JDefinedClass clazz) {
        final JMethod getRootNodeMethod = clazz.method(JMod.PUBLIC, Node.class, "getRootNode");
        final JMethod getNodeMethod = clazz.method(JMod.PRIVATE, Node.class, "getNode");
        getRootNodeMethod.body().directStatement("return model;");
        getNodeMethod.body().directStatement("return getRootNode();");
    }

    private String[] getReplaceList(final String descriptorName, final MetadataDescriptor descriptor, final String factoryContext) {
        int itemCounter = 0;
        final String[] strArray = new String[8];
        strArray[itemCounter++] = descriptorName;
        final Enumeration<?> em = descriptor.getNamespaces().keys();
        while (em.hasMoreElements()) {
            final String key = (String) em.nextElement();
            final String value = (String) descriptor.getNamespaces().get(key);
            strArray[itemCounter++] = key;
            strArray[itemCounter++] = value;
        }
        if (descriptor.getNamespaces().isEmpty()) {
            strArray[itemCounter++] = "dummy1";
            strArray[itemCounter++] = "dummy2";
            strArray[itemCounter++] = "dummy3";
            strArray[itemCounter++] = "dummy4";
            strArray[itemCounter++] = "dummy5";
            strArray[itemCounter++] = "dummy6";
        }
        strArray[itemCounter++] = factoryContext;
        return strArray;
    }

    private String[] getNamespaceMethods(final MetadataDescriptor descriptor) {
        if (descriptor.getNamespaces().isEmpty()) {
            return NS_NO_DEFAULT_METHOD_LIST;
        }
        return NS_METHOD_LIST;
    }
}

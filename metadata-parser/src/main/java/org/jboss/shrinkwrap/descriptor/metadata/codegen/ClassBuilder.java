package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.descriptor.impl.base.ChildNodeInitializer;
import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataDescriptor;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataItem;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataJavaDoc;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataParserPath;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class ClassBuilder {

    private static final Logger log = Logger.getLogger(ClassBuilder.class.getName());

    public void generate(final Metadata metadata, final String pathToMetadata, final List<? extends MetadataJavaDoc> javadocTags, final MetadataParserPath path) throws Exception {
        generateClasses(metadata, CommonClassesUtil.INSTANCE.getCommonClasses(metadata, path), path.getPathToApi(), true);
        generateClasses(metadata, CommonClassesUtil.INSTANCE.getCommonClasses(metadata, path), path.getPathToImpl(), false);
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private void generateClasses(final Metadata metadata, final Map<String, CommonClassItem> commonClassesMap, final String path, final boolean isApi) throws Exception {
        EnumBuilder.createEnums(metadata, path, isApi);
        for (final MetadataItem metadataClass : metadata.getClassList()) {
            if (isDescriptorRootElement(metadata, metadataClass) || !isGenerateClasses(metadata, metadataClass)) {
                continue;
            } else if (metadataClass.getElements().size() == 1 && metadataClass.getElements().get(0).getType().equals("xsd:ID")) {
                continue;
            }
            final String classNameTmp = getClassName(metadataClass, isApi);
            final String className = getClassName(metadataClass, isApi); // getRealClassName(metadataClass, isApi);
            final String fullyQualifiedFactoryName = getPackage(metadataClass, isApi) + "." + classNameTmp;
            final JCodeModel jcm = new JCodeModel();
            final JDefinedClass clazz = BuilderUtil.getClass(jcm, fullyQualifiedFactoryName, isApi);
            if (isApi) {
//                final String commonClassName = BuilderUtil.getCommonName(className);
//                final String commonNamespace = getCommonNamespace(metadata, metadataClass);
//                final String fqCommonClassName = CodeGen.getPascalizeCase(commonNamespace + commonClassName);
//                final CommonClassItem commonClassItem = commonClassesMap.get(fqCommonClassName + ".java");
//                if (commonClassItem != null) {
//                    final String commonPackageName = commonClassItem.getPackageAPi().replaceAll("[0-9]*$", "");
//                    final JClass commonClazz =jcm.directClass(commonPackageName + "." + fqCommonClassName);
//                    final JClass realClassname = jcm.ref(metadataClass.getPackageApi() + "." + className);
//                    List<JClass> narrowList = getNarrowList(jcm, realClassname, commonClassItem);
//                    final JClass nn = commonClazz.narrow(narrowList);
//                    clazz._implements(nn);
//                    clazz.direct("// extends: " + commonPackageName + "  " + commonClazz.toString() + "\n");
//                }
            } else {
                final String fqnApi = getPackage(metadataClass, true) + "." + getClassName(metadataClass, true);
                final JClass apiClass = jcm.directClass(fqnApi);
                clazz._implements(apiClass);
                clazz._implements(ChildNodeInitializer.class);
                clazz.field(JMod.PRIVATE, Node.class, "childNode");
                clazz.field(JMod.PRIVATE, Node.class, "detachedNode");
                clazz.field(JMod.PRIVATE, boolean.class, "isDetached", JExpr.TRUE);
                addConstructors(clazz);
                addGetNode(clazz);
                addChildNodeInitializerMethods(clazz);
            }

            for (final MetadataElement element : getElementList(metadataClass, metadata)) {
                clazz.direct(element.asClassComment());
                for (MethodGeneratorContract methodGenerator : getMethodGenerators()) {
                    if (methodGenerator.addMethods(clazz, metadata, element, className, isApi)) {
                        break;
                    }
                }
//
//
//                clazz.direct("// isEnum: " + BuilderUtil.isEnum(metadata, element));
//                if (element.getType().endsWith("text")) {
//                    TextTypeBuilder.addTextTypeMethods(clazz, metadata, element, className, isApi);
//                } else if (BuilderUtil.isEmptyBooleanType(element.getType())) {
//                    BooleanTypeBuilder.addAttributeMethods(clazz, element, className, isApi);
//                } else if (BuilderUtil.isEnum(metadata, element)) {
//                    EnumBuilder.addEnumMethods(clazz, metadata, element, className, isApi);
//                } else if (BuilderUtil.isAttribute(metadata, element)) {
//                    AttributeBuilder.addAttributeMethods(clazz, element, className, isApi);
//                } else if (BuilderUtil.isDataType(metadata, element)) {
//                    DataTypeBuilder.addDataytpeMethods(clazz, metadata, element, className, isApi);
//                } else {
//                    ElementBuilder.addElementMethods(clazz, metadata, element, className, isApi);
//                }
            }

            final File file = new File(path);
            jcm.build(file);
        }
    }

    private Set<MetadataElement> getElementList(final MetadataItem metadataClass, final Metadata metadata) {
        final Set<MetadataElement> elementList = new HashSet<MetadataElement>();
        if (metadataClass.getElements() != null) {
            elementList.addAll(metadataClass.getElements());
        }
        if (metadataClass.getReferences() != null) {
            elementList.addAll(includeGroupRefs(metadataClass, metadata));
        }
        return elementList;
    }

    private Set<MetadataElement> includeGroupRefs(final MetadataItem metadataClass, final Metadata metadata) {
        final Set<MetadataElement> elementList = new HashSet<MetadataElement>();
        for (final MetadataElement groupElement : metadataClass.getReferences()) {
            final MetadataItem groupItem = BuilderUtil.findGroup(metadata, groupElement);
            if (groupItem != null) {
                if ("unbounded".equals(groupElement.getMaxOccurs())) {
                    for (final MetadataElement el : groupItem.getElements()) {
                       el.setMaxOccurs("unbounded");
                    }
                }
                elementList.addAll(groupItem.getElements());
                if (groupItem.getReferences() != null) {
                    for (final MetadataElement subGroupElement : groupItem.getReferences()) {
                        elementList.addAll(includeGroupRefs(groupItem, metadata));
                    }
                }
            }
        }
        return elementList;
    }

    private String getPackage(final MetadataItem metadataClass, final boolean isApi) {
        if (isApi) {
            return metadataClass.getPackageApi();
        } else {
            return metadataClass.getPackageImpl();
        }
    }

    private String getClassName(final MetadataItem metadataClass, final boolean isApi) {
        return getRealClassName(metadataClass, isApi);
//        if (isApi) {
//            return CodeGen.getPascalizeCase(metadataClass.getName() + "Tmp");
//        } else {
//            return CodeGen.getPascalizeCase(metadataClass.getName() + "ImplTmp");
//        }
    }

    private String getRealClassName(final MetadataItem metadataClass, final boolean isApi) {
        if (isApi) {
            return CodeGen.getPascalizeCase(metadataClass.getName());
        } else {
            return CodeGen.getPascalizeCase(metadataClass.getName() + "Impl");
        }
    }

    private void addConstructors(final JDefinedClass clazz) {
        clazz.constructor(JMod.PUBLIC);

        final JMethod createChildConstr = clazz.constructor(JMod.PUBLIC);
        createChildConstr.param(JMod.FINAL, String.class, "nodeName");
        createChildConstr.param(JMod.FINAL, Node.class, "node");
        createChildConstr.body().directStatement("this.childNode = node.createChild(nodeName);");

        final JMethod assignChildConstr = clazz.constructor(JMod.PUBLIC);
        assignChildConstr.param(JMod.FINAL, String.class, "nodeName");
        assignChildConstr.param(JMod.FINAL, Node.class, "node");
        assignChildConstr.param(JMod.FINAL, Node.class, "childNode");
        assignChildConstr.body().directStatement("this.childNode = childNode;");
    }

    private void addGetNode(final JDefinedClass clazz) {
        final JMethod getNodeMethod = clazz.method(JMod.PUBLIC, Node.class, "getNode");
        final JBlock block = getNodeMethod.body();
        block._if(JExpr.ref("isDetached").not())._then()._return(JExpr.direct("childNode"));
        block._if(JExpr.ref("detachedNode").eq(JExpr._null()))._then().block().directStatement("detachedNode = new Node(\"DetachedNode\");");
        block._return(JExpr.direct("detachedNode"));
    }

    private void addChildNodeInitializerMethods(final JDefinedClass clazz) {
        final JMethod initializeMethod = clazz.method(JMod.PUBLIC, void.class, "initialize");
        initializeMethod.param(JMod.FINAL, String.class, "nodeName");
        initializeMethod.param(JMod.FINAL, Node.class, "node");
        final JBlock block = initializeMethod.body();
        final JConditional ifConditional = block._if(JExpr.ref("isDetached"));
        final JBlock ifBlock = ifConditional._then();
        final JConditional ifConditional2 = ifBlock._if(JExpr.ref("detachedNode").eq(JExpr._null()).not());
        ifConditional2._then().block()
                 .directStatement("childNode = node.createChild(nodeName); Node.copyFromTo(detachedNode, childNode);");
        ifConditional2._else().block()
                 .directStatement("childNode = node.createChild(nodeName);");
        ifBlock.directStatement("isDetached = false;");
        ifConditional._else().block().directStatement("throw new IllegalArgumentException();");

        final JMethod assignMethod = clazz.method(JMod.PUBLIC, void.class, "assign");
        assignMethod.param(JMod.FINAL, String.class, "nodeName");
        assignMethod.param(JMod.FINAL, Node.class, "node");
        assignMethod.body().directStatement("childNode = node; isDetached = false;");
    }

    private List<JClass> getNarrowList(final JCodeModel jcm, final JClass baseClassname, final CommonClassItem commonClassItem) {
        final List<JClass> narrowList = new ArrayList<JClass>();
        narrowList.add(baseClassname);
        if (commonClassItem.getExtendsList() != null) {
            for (String extendsClassName : commonClassItem.getExtendsList()) {
                if (!"dummy".equals(extendsClassName)) {
                    narrowList.add(jcm.ref(CodeGen.getPascalizeCase(extendsClassName)));
                }
            }
        }
        return narrowList;
    }

    private boolean isDescriptorRootElement(final Metadata metadata, final MetadataItem metadataClass) {
        final String typeName = metadataClass.getNamespace() + ":" + metadataClass.getName();
        for (final MetadataDescriptor descriptor : metadata.getMetadataDescriptorList()) {
            if (typeName.equals(descriptor.getRootElementType()) && !typeName.equals("javaee:uicomponent-attributeType")) {
                return true;
            }
        }
        return false;
    }

    private boolean isGenerateClasses(final Metadata metadata, final MetadataItem metadataClass) {
        for (final MetadataDescriptor descriptor : metadata.getMetadataDescriptorList()) {
            if (descriptor.getPackageApi().equals(metadataClass.getPackageApi())) {
                return descriptor.isGenerateClasses();
            }
        }
        return false;
    }

    private String getCommonNamespace(final Metadata metadata, final MetadataItem metadataClass) {
        final String commonPackage = metadataClass.getPackageApi().replaceAll("[0-9]*$", "");
        for (final MetadataDescriptor descriptor : metadata.getMetadataDescriptorList()) {
            if (descriptor.getCommon() != null) {
                if (commonPackage.equals(descriptor.getCommon().getCommonNamespace())) {
                    return descriptor.getCommon().getCommonNamespace();
                }
            }
        }
        return metadataClass.getNamespace();
    }

    /**
     * Returns a list of <code>MethodGeneratorContract</code>. Please don't change the order!
     * @return
     */
    private List<MethodGeneratorContract> getMethodGenerators() {
        final List<MethodGeneratorContract> generatorList = new ArrayList<MethodGeneratorContract>();
        generatorList.add(new TextTypeBuilder());
        generatorList.add(new BooleanTypeBuilder());
        generatorList.add(new EnumBuilder());
        generatorList.add(new AttributeBuilder());
        generatorList.add(new DataTypeBuilder());
        generatorList.add(new ElementBuilder());
        return generatorList;
    }
}

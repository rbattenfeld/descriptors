package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.descriptor.impl.base.ChildNodeInitializer;
import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
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
        generateClasses(metadata, path.getPathToApi(), true);
        generateClasses(metadata, path.getPathToImpl(), false);
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private void generateClasses(final Metadata metadata, final String path, final boolean isApi) throws Exception {
        EnumBuilder.createEnums(metadata, path);
        for (final MetadataItem metadataClass : metadata.getClassList()) {
            final String className = getClassName(metadataClass, isApi);
            final String fullyQualifiedFactoryName = getPackage(metadataClass, isApi) + "." + className;
            final JCodeModel jcm = new JCodeModel();
            final JDefinedClass clazz = BuilderUtil.getClass(jcm, fullyQualifiedFactoryName, isApi);
            if (isApi) {

            } else {
               final String fqnApi = getPackage(metadataClass, true) + "." + getClassName(metadataClass, true);
               final JClass apiClass = jcm.directClass(fqnApi);
               clazz._implements(apiClass);
               clazz._implements(ChildNodeInitializer.class);
               clazz.field(JMod.PRIVATE, Node.class, "childNode");
               clazz.field(JMod.PRIVATE, Node.class, "detachedNode");
               clazz.field(JMod.PRIVATE, boolean.class, "isDetached", JExpr.TRUE);
               addGetNode(clazz);
               addChildNodeInitializerMethods(clazz);
            }

            for (final MetadataElement element : getElementList(metadataClass, metadata)) {
                if (element.getType().endsWith("text")) {
                    //
                } else if (BuilderUtil.isEmptyBooleanType(element.getType())) {
                    //
                } else if (BuilderUtil.isEnum(metadata, metadataClass)) {
                    EnumBuilder.addEnumMethods(clazz, metadata, element, className, isApi);
                } else if (BuilderUtil.isAttribute(metadata, element)) {
                    AttributeBuilder.addAttributeMethods(clazz, element, className, isApi);
                } else if (BuilderUtil.isDataType(metadata, element)) {
                    DataTypeBuilder.addDataytpeMethods(clazz, metadata, element, className, isApi);
                } else {
                    ElementBuilder.addElementMethods(clazz, metadata, element, className, isApi);
                }
            }

            final File file = new File(path);
            jcm.build(file);
        }
    }

    private Set<MetadataElement> getElementList(final MetadataItem metadataClass, final Metadata metadata) {
        final Set<MetadataElement> elementOrReferenceList = new HashSet<MetadataElement>();
        if (metadataClass.getElements() != null) {
            elementOrReferenceList.addAll(metadataClass.getElements());
        }
        if (metadataClass.getReferences() != null) {
            for (final MetadataElement groupElement : metadataClass.getReferences()) {
                final MetadataItem groupItem = BuilderUtil.findGroup(metadata, groupElement);
                if (groupItem != null) {
                    if ("unbounded".equals(groupElement.getMaxOccurs())) {
                        for (final MetadataElement el : groupItem.getElements()) {
                           el.setMaxOccurs("unbounded");
                        }
                    }
                    elementOrReferenceList.addAll(groupItem.getElements());
                }
            }
        }
        return elementOrReferenceList;
    }

    private String getPackage(final MetadataItem metadataClass, final boolean isApi) {
        if (isApi) {
            return metadataClass.getPackageApi();
        } else {
            return metadataClass.getPackageImpl();
        }
    }

    private String getClassName(final MetadataItem metadataClass, final boolean isApi) {
        if (isApi) {
            return CodeGen.getPascalizeCase(metadataClass.getName() + "Tmp");
        } else {
            return CodeGen.getPascalizeCase(metadataClass.getName() + "TmpImpl");
        }
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

}

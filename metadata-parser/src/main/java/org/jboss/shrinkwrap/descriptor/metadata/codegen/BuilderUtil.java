package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jboss.shrinkwrap.descriptor.metadata.BaseMetadataItem;
import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataDescriptor;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataEnum;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataItem;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataParserPath;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

public class BuilderUtil {
    private static Set<String> emtpyBooleanSet = new HashSet<String>();

    static {
        emtpyBooleanSet.add("javaee:emptyType");
        emtpyBooleanSet.add("javaee:ordering-othersType");
        emtpyBooleanSet.add("javaee:facelet-taglib-extensionType");
        emtpyBooleanSet.add("javaee:facelet-taglib-tag-behavior-extensionType");
        emtpyBooleanSet.add("javaee:facelet-taglib-tag-component-extensionType");
        emtpyBooleanSet.add("javaee:facelet-taglib-tag-converter-extensionType");
        emtpyBooleanSet.add("javaee:facelet-taglib-tag-extensionType");
        emtpyBooleanSet.add("javaee:facelet-taglib-tag-validator-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-application-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-attribute-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-behavior-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-component-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-converter-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-facet-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-factory-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-lifecycle-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-managed-bean-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-navigation-rule-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-null-valueType");
        emtpyBooleanSet.add("javaee:faces-config-ordering-othersType");
        emtpyBooleanSet.add("javaee:faces-config-property-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-render-kit-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-renderer-extensionType");
        emtpyBooleanSet.add("javaee:faces-config-validator-extensionType");
        emtpyBooleanSet.add("javaee:partial-response-extensionType");
        emtpyBooleanSet.add("javaee:extensibleType");
    }

    public static JDefinedClass getClass(final JCodeModel jcm, final String fqn, final boolean isApi) throws JClassAlreadyExistsException {
        if (isApi) {
            return jcm._class(fqn, ClassType.INTERFACE);
        } else {
            return jcm._class(fqn, ClassType.CLASS);
        }
    }

    public static String replaceAll(final String template, final boolean isApi, final String[] searchList, final String[] replaceList) {
        final String methodStr = StringUtils.replaceEachRepeatedly(template, searchList, replaceList);
        if (isApi) {
            int pos = methodStr.indexOf('{');
            return methodStr.substring(0,  pos) + ";\n";
        } else {
            return methodStr.toString();
        }
    }

    public static String checkReservedWords(final String keyWord) {
        if (keyWord.equals("class")) { return "clazz"; }
        if (keyWord.equals("Class")) { return "clazz"; }
        if (keyWord.equals("default")) { return "_default"; }
        if (keyWord.equals("Default")) { return "_default"; }
        if (keyWord.equals("package")) { return "_package"; }
        if (keyWord.equals("if")) { return "_if"; }
        if (keyWord.equals("new")) { return "_new"; }
        if (keyWord.equals("Set")) { return "_Set"; }
        else { return keyWord; }
    }

    public static Class<?> getDataType(final String xsdType) {
        if (xsdType.contains(":") && !xsdType.startsWith("xsd")) {
            final String[] items = xsdType.split(":", -1);
            return getDataType(items[1]);
        }
        if (xsdType.equals("xsd:long")) { return Long.class; }
        if (xsdType.equals("xsd:decimal")) { return String.class; }
        if (xsdType.equals("xsd:integer")) { return Integer.class; }
        if (xsdType.equals("xsd:int")) { return Integer.class; }
        if (xsdType.equals("xsd:string")) { return String.class; }
        if (xsdType.equals("xsdIntegerType")) { return Integer.class; }
        if (xsdType.equals("xsd:positiveInteger")) { return Integer.class; }
        if (xsdType.equals("positiveInteger")) { return Integer.class; }
        if (xsdType.equals("nonNegativeInteger")) { return Integer.class; }
        if (xsdType.equals("xsd:nonNegativeInteger")) { return Integer.class; }
        if (xsdType.equals("integer")) { return Integer.class; }
        if (xsdType.equals("int")) { return Integer.class; }
        if (xsdType.equals("xsdStringType")) { return String.class; }
        if (xsdType.equals("xsdNMTOKENType")) { return String.class; }
        if (xsdType.equals("string")) { return String.class; }
        if (xsdType.equals("xsd:QName")) { return String.class; }
        if (xsdType.equals("xsd:anyURI")) { return String.class; }
        if (xsdType.equals("xsd:NMTOKEN")) { return String.class; }
        if (xsdType.equals("xsd:NCName")) { return String.class; }
        if (xsdType.equals("xsd:token")) { return String.class; }
        if (xsdType.equals("xsd:ID")) { return String.class; }
        if (xsdType.equals("nonEmptyStringType")) { return String.class; }
        if (xsdType.equals("xsd:boolean")) { return Boolean.class; }
        if (xsdType.equals("xsdBooleanType")) { return Boolean.class; }
        if (xsdType.equals("token")) { return String.class; }
        if (xsdType.equals("long")) { return Long.class; }
        if (xsdType.equals("xsd:dateTime")) { return Date.class; }
        if (xsdType.equals("String")) { return String.class; }
        else { return String.class; }
    }

    public static boolean isEnum(final Metadata metadata, final MetadataElement element) {
        for (final MetadataEnum enumElement : metadata.getEnumList()) {
            final String[] items = element.getType().split(":", -1);
            if (items.length == 2) {
                if (enumElement.getName().equals(items[1]) && enumElement.getNamespace().equals(items[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDataType(final Metadata metadata, final MetadataElement element) {
        final String mappedTo = getDatatypeMappedTo(metadata, element);
        if (element.getType().startsWith("xsd:")) {
            return true;
        } else if (!mappedTo.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isAttribute(final Metadata metadata, final MetadataElement element) {
        return element.getIsAttribute(); // && !isDataType(metadata, element);
    }

    public static String getDatatypeMappedTo(final Metadata metadata, final MetadataElement element) {
        final String[] items = element.getType().split(":", -1);
        if (items.length == 2) {
            for (final MetadataItem datatypeItem : metadata.getDataTypeList()) {
                if (datatypeItem.getName().equals(items[1]) && datatypeItem.getNamespace().equals(items[0])) {
                    return datatypeItem.getMappedTo();
                }
            }
        }
        return "";
    }

    public static MetadataItem findClass(final Metadata metadata, final String type) {
        final String[] items = type.split(":", -1);
        if (items.length == 2) {
            return findClass(metadata, items[0], items[1]);
        }
        return null;
    }

    public static MetadataItem findClass(final Metadata metadata, final MetadataElement element) {
        final String[] items = element.getType().split(":", -1);
        if (items.length == 2) {
            return findClass(metadata, items[0], items[1]);
        }
        return null;
    }

    public static MetadataItem findClass(final Metadata metadata, final String namespace, final String name) {
        for (MetadataItem classType : metadata.getClassList()) {
            if (classType.getName().equals(name) && classType.getNamespace().equals(namespace)) {
                return classType;
            }
        }
        return null;
    }

    public static MetadataItem findGroup(final Metadata metadata, final MetadataElement element) {
        final String[] items = element.getRef().split(":", -1);
        if (items.length == 2) {
            for (MetadataItem classType : metadata.getGroupList()) {
                if (classType.getName().equals(items[1]) && classType.getNamespace().equals(items[0])) {
                    return classType;
                }
            }
        }
        return null;
    }

    public static boolean isEmptyBooleanType(final String type) {
        return emtpyBooleanSet.contains(type);
    }

    public static String getCommonPackageName(final String packageName) {
        return packageName.replace("[0-9]+$", "");
    }

    public static String getCommonName(final String className) {
        if (className.endsWith("Type")) {
            return className.replace("Type", "CommonType");
        } else if (className.endsWith("type")) {
            return className.replace("type", "CommonType");
        } else {
            return className + "CommType";
        }
    }

    public static String getClassName(final BaseMetadataItem metadataItem, final boolean isApi) {
        if (isApi) {
            return CodeGen.getPascalizeCase(metadataItem.getName());
        } else {
            return CodeGen.getPascalizeCase(metadataItem.getName() + "Impl");
        }
    }

    public static String getPackage(final BaseMetadataItem metadataItem, final boolean isApi) {
        if (isApi) {
            return metadataItem.getPackageApi();
        } else {
            return metadataItem.getPackageImpl();
        }
    }

    public static Map<String, List<String>> getCommonClasses(final Metadata metadata, final MetadataParserPath path) throws IOException {
        final Map<String, List<String>> existingCommonClassMap = new HashMap<String, List<String>>();
        final Set<String> commonPathSet = new HashSet<String>();
        final List<File> fileList = new ArrayList<File>();
        for (final MetadataDescriptor descr : metadata.getMetadataDescriptorList()) {
            if (descr.getCommon() != null) {
                final String pathTo = descr.getCommon().getPathToCommonApi();
                final String commonApi = descr.getCommon().getCommonApi().replace('.', '/');
                commonPathSet.add(pathTo + "/" + commonApi);
            } else if (descr.isGenerateCommonClasses()) {
                final String pathTo = path.getPathToApi();
                final String commonApi = descr.getPackageApi().replaceAll("[0-9]*$", "").replace('.', '/');
                commonPathSet.add(pathTo + "/" + commonApi);
            }
        }

        for (final String pathCommonApi : commonPathSet) {
            listFiles(fileList, pathCommonApi);
        }

        final JavaProjectBuilder builder = new JavaProjectBuilder();
        for (final File file : fileList) {
            if (file.getName().endsWith(".java")) {
                final List<String> extendsList = new ArrayList<String>();
                existingCommonClassMap.put(file.getName(), extendsList);
                final JavaSource src = builder.addSource(file);
                final JavaClass class1  = src.getClasses().get(0);
                final List<JavaAnnotation> annotationList = class1.getAnnotations();
                for (JavaAnnotation annotation : annotationList) {
                    final  AnnotationValue value = annotation.getProperty("common");
                    final List<String> commonExtendsList = (List<String>)value.getParameterValue();
                    for (String commonClass : commonExtendsList) {
                        extendsList.add(commonClass.replace('"', ' ').trim());
                    }
                }
            }
        }
        return existingCommonClassMap;
    }

    private static void listFiles(final List<File> list, final String directoryName) {
        final File directory = new File(directoryName);
        if (directory.isDirectory()) {
            final File[] fList = directory.listFiles();
            for (final File file : fList) {
                if (file.isFile()) {
                    list.add(file);
                } else if (file.isDirectory()) {
                    listFiles(list, file.getAbsolutePath());
                }
            }
        }
    }

    public static List<String> findCommonClasses(final Metadata metadata) {
        final List<String> classList = new ArrayList<String>();
        for (MetadataDescriptor descr : metadata.getMetadataDescriptorList()) {
            if (descr.getCommon() != null) {
                traverseClasses(metadata, classList, descr.getRootElementType());
            } else {
                if (descr.isGenerateClasses()) {
                    final String packageApi = descr.getPackageApi();
                    for (MetadataItem item : metadata.getClassList()) {
                        if (packageApi.equals(item.getPackageApi())) {
                            final String type = item.getNamespace() + ":" + item.getName();
                            if (!classList.contains(type)) {
                                classList.add(type);
                            }
                        }
                    }
                }
            }
        }
        return classList;
    }

    private static void traverseClasses(final Metadata metadata, final List<String> classList, final String elementType) {
        final String[] elItems = elementType.split(":", -1);
        if (elItems.length == 2) {
            for (MetadataItem item : metadata.getClassList()) {
                if (item.getNamespace().equals(elItems[0]) && item.getName().equals(elItems[1])) {
                    for (MetadataElement element : item.getElements()) {
                        if (!isDataType(metadata, element)) {
                            if (!classList.contains(element.getType())) {
                                classList.add(element.getType());
                                traverseClasses(metadata, classList, element.getType());
                            }
                        }
                    }
                    for (MetadataElement element : item.getReferences()) {
                        if (!isDataType(metadata, element)) {
                            if (!classList.contains(element.getRef())) {
                                classList.add(element.getRef());
                                traverseClasses(metadata, classList, element.getRef());
                            }
                        }
                    }
                }
            }
            for (MetadataItem item : metadata.getGroupList()) {
                if (item.getNamespace().equals(elItems[0]) && item.getName().equals(elItems[1])) {
                    for (MetadataElement element : item.getElements()) {
                        if (!isDataType(metadata, element)) {
                            if (!classList.contains(element.getType())) {
                                classList.add(element.getType());
                                traverseClasses(metadata, classList, element.getType());
                            }
                        }
                    }
                    for (MetadataElement element : item.getReferences()) {
                        if (!isDataType(metadata, element)) {
                            if (!classList.contains(element.getRef())) {
                                classList.add(element.getRef());
                                traverseClasses(metadata, classList, element.getRef());
                            }
                        }
                    }
                }
            }
        }
    }

    public static Set<MetadataElement> getElementList(final MetadataItem metadataClass, final Metadata metadata) {
        final Set<MetadataElement> elementList = new HashSet<MetadataElement>();
        if (metadataClass.getElements() != null) {
            elementList.addAll(metadataClass.getElements());
        }
        if (metadataClass.getReferences() != null) {
            elementList.addAll(includeGroupRefs(metadataClass, metadata));
        }
        return elementList;
    }

    public static Set<MetadataElement> includeGroupRefs(final MetadataItem metadataClass, final Metadata metadata) {
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

    /**
     * Returns a list of <code>MethodGeneratorContract</code>. Please don't change the order!
     * @return
     */
    public static List<MethodGeneratorContract> getMethodGenerators() {
        final List<MethodGeneratorContract> generatorList = new ArrayList<MethodGeneratorContract>();
        generatorList.add(new TextTypeBuilder());
        generatorList.add(new BooleanTypeBuilder());
        generatorList.add(new EnumBuilder());
        generatorList.add(new AttributeBuilder());
        generatorList.add(new DataTypeBuilder());
        generatorList.add(new ElementBuilder());
        return generatorList;
    }

    public static String getPath(final MetadataParserPath path, final boolean isApi) {
        if (isApi) {
            return path.getPathToApi();
        }
        return path.getPathToImpl();
    }

    public static boolean isDescriptorRootElement(final Metadata metadata, final MetadataItem metadataClass) {
        final String typeName = metadataClass.getNamespace() + ":" + metadataClass.getName();
        for (final MetadataDescriptor descriptor : metadata.getMetadataDescriptorList()) {
            if (typeName.equals(descriptor.getRootElementType()) && !typeName.equals("javaee:uicomponent-attributeType")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGenerateClasses(final Metadata metadata, final MetadataItem metadataClass) {
        for (final MetadataDescriptor descriptor : metadata.getMetadataDescriptorList()) {
            if (descriptor.getPackageApi().equals(metadataClass.getPackageApi())) {
                return descriptor.isGenerateClasses();
            }
        }
        return false;
    }

    public static String getCommonNamespace(final Metadata metadata, final MetadataItem metadataClass) {
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

    public static JFieldVar checkFixedValue(final JDefinedClass clazz, final MetadataElement element, final String elementName, Class<?> type) {
        if (element.getFixedValue() != null && !element.getFixedValue().isEmpty()) {
            return clazz.field(JMod.PUBLIC | JMod.STATIC |JMod.FINAL, type, elementName.toUpperCase());
        }
        return null;
    }
}

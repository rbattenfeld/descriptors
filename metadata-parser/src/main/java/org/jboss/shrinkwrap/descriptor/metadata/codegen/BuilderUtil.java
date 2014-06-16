package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataEnum;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataItem;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

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

    public static boolean isEnum(final Metadata metadata, final MetadataItem metadataClass) {
        for (final MetadataEnum enumElement : metadata.getEnumList()) {
            if (enumElement.getName().equals(metadataClass.getName()) && enumElement.getNamespace().equals(metadataClass.getNamespace())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDataType(final Metadata metadata, final MetadataElement element) {
        final String mappedTo = getDatatypeMappedTo(metadata, element);
        System.out.println("elementtype: " + element.getType() + " mappedTo: " + mappedTo);
        if (element.getType().startsWith("xsd:")) {
            return true;
        } else if (!mappedTo.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isAttribute(final Metadata metadata, final MetadataElement element) {
        return element.getIsAttribute() && !isDataType(metadata, element);
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

    public static MetadataItem findClass(final Metadata metadata, final MetadataElement element) {
        final String[] items = element.getType().split(":", -1);
        if (items.length == 2) {
            for (MetadataItem classType : metadata.getClassList()) {
                if (classType.getName().equals(items[1]) && classType.getNamespace().equals(items[0])) {
                    return classType;
                }
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
}

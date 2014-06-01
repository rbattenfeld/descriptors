package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class BuilderUtil {

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
}

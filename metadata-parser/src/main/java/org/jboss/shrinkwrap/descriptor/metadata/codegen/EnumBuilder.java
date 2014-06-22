package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataEnum;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataJavaDoc;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataParserPath;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class EnumBuilder implements MethodGeneratorContract {

    private static final Logger log = Logger.getLogger(EnumBuilder.class.getName());

    private static final String[] SEARCH_LIST = new String[] {"DATATYPE", "ELEMENTNAME_P", "ELEMENTNAME_C", "CLASSNAME_P", "ELEMENTNAME_O", "ELEMENTTYPE_P"};

    private static final String METHOD_SET_AS_ENUM_TYPE = "\n"
        + "    /**\n"
        + "     * Sets the <code>ELEMENTNAME_O</code> element\n"
        + "     * @param ELEMENTNAME_C the value for the element <code>ELEMENTNAME_O</code> \n"
        + "     * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "     */\n"
        + "    public CLASSNAME_P ELEMENTNAME_C(ELEMENTTYPE_P ELEMENTNAME_C) {\n"
        + "        getNode().getOrCreate(\"ELEMENTNAME_O\").text(ELEMENTNAME_C);\n"
        + "        return this;\n"
        + "    }\n";

    private static final String ENUM_GET_FROM_STRING_VALUE = "\n"
        + "    public static ELEMENTTYPE_P getFromStringValue(String value) {\n"
        + "        for (ELEMENTTYPE_P type : ELEMENTTYPE_P.values()) {\n"
        + "            if (value != null && type.toString().equals(value)) {\n"
        + "                return type;\n"
        + "            }\n"
        + "        }\n"
        + "        return null;\n"
        + "    }\n";

    private static final String METHOD_SET_AS_STRING_TYPE = "\n"
        + "    /**\n"
        + "     * Sets the <code>ELEMENTNAME_O</code> element\n"
        + "     * @param ELEMENTNAME_C the value for the element <code>ELEMENTNAME_O</code> \n"
        + "     * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "     */\n"
        + "    public CLASSNAME_P ELEMENTNAME_C(String ELEMENTNAME_C) {\n"
        + "        getNode().getOrCreate(\"ELEMENTNAME_O\").text(ELEMENTNAME_C);\n"
        + "        return this;\n"
        + "    }\n";

    private static final String METHOD_GET_AS_ENUM_TYPE = "\n"
        + "    /**\n"
        + "     * Returns the <code>ELEMENTNAME_O</code> element\n"
        + "     * @return the value found for the element <code>ELEMENTNAME_O</code> \n"
        + "     */\n"
        + "    public ELEMENTTYPE_P getELEMENTNAME_P() {\n"
        + "        return ELEMENTTYPE_P.getFromStringValue(getNode().getTextValueForPatternName(\"ELEMENTNAME_O\"));\n"
        + "    }\n";

    private static final String METHOD_GET_AS_ENUM_STRING = "\n"
        + "    /**\n"
        + "     * Returns the <code>ELEMENTNAME_O</code> element\n"
        + "     * @return the value found for the element <code>ELEMENTNAME_O</code> \n"
        + "     */\n"
        + "    public String  getELEMENTNAME_PAsString() {\n"
        + "        return getNode().getTextValueForPatternName(\"ELEMENTNAME_O\");\n"
        + "    }\n";

    private static final String METHOD_REM_AS_ENUM_TYPE = "\n"
        + "    /**\n"
        + "     * Removes the <code>ELEMENTNAME_O</code> attribute \n"
        + "     * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "     */\n"
        + "    public CLASSNAME_P removeELEMENTNAME_P() {\n"
        + "        getNode().removeAttribute(\"ELEMENTNAME_O\");\n"
        + "        return this;\n"
        + "    }\n";

    private static final String[] METHOD_LIST = new String[] {
        METHOD_SET_AS_ENUM_TYPE,
        METHOD_SET_AS_STRING_TYPE,
        METHOD_GET_AS_ENUM_TYPE,
        METHOD_GET_AS_ENUM_STRING,
        METHOD_REM_AS_ENUM_TYPE};

    public void generate(final Metadata metadata, final String pathToMetadata, final List<? extends MetadataJavaDoc> javadocTags, final MetadataParserPath path) throws Exception {
        generateEnums(metadata, path.getPathToApi(), true);
    }

    @Override
    public boolean addMethods(final JDefinedClass clazz, final  Metadata metadata, final MetadataElement element, final String className, final boolean isApi) throws Exception {
        if (BuilderUtil.isEnum(metadata, element)) {
            generateEnumMethod(clazz, className, metadata, element, isApi);
            return true;
        }
        return false;
    }

    public static void createEnums(final Metadata metadata, final String path, final boolean isApi) throws Exception {
        if (isApi) {
            for (final MetadataEnum metadataEnum : metadata.getEnumList()) {
                final String className = CodeGen.getPascalizeCase(metadataEnum.getName());
                final String fullyQualifiedEnumName = metadataEnum.getPackageApi() + "." + className;
                final JCodeModel jcm = new JCodeModel();
                final JDefinedClass enumClass = jcm._class(fullyQualifiedEnumName, ClassType.ENUM);
                enumClass.field(JMod.PRIVATE, String.class, "value");

                final JMethod method = enumClass.constructor(JMod.NONE);
                method.param(String.class, "value");
                method.body().directStatement("this.value = value;");

                for (final String value : metadataEnum.getValueList()) {
                    log.info(value);
                    enumClass.enumConstant("_" + value.replaceAll("\\.", "_").toUpperCase() + "(\"" + value + "\")");
                }

                final JMethod toStringMethod = enumClass.method(JMod.PUBLIC, String.class, "toString");
                toStringMethod.body()._return(JExpr.direct("value"));

                enumClass.direct(BuilderUtil.replaceAll(ENUM_GET_FROM_STRING_VALUE, false, new String[] {"ELEMENTTYPE_P"}, new String[] {className}));

                final File file = new File(path);
                jcm.build(file);
            }
        }
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private void generateEnums(final Metadata metadata, final String path, final boolean isApi) throws Exception {
        EnumBuilder.createEnums(metadata, path, isApi);
    }

    private MetadataEnum getEnum(final Metadata metadata, final MetadataElement element) {
        final String[] items = element.getType().split(":", -1);
        if (items.length == 2) {
            for (MetadataEnum enumType : metadata.getEnumList()) {
                if (enumType.getName().equals(items[1]) && enumType.getNamespace().equals(items[0])) {
                    return enumType;
                }
            }
        }
        return null;
    }

    private void generateEnumMethod(final JDefinedClass clazz, final String className, final Metadata metadata, final MetadataElement element, final boolean isApi) throws Exception {
        final MetadataEnum enumType = getEnum(metadata, element);
        if (enumType != null) {
            final String elementName = BuilderUtil.checkReservedWords(CodeGen.getCamelCase(element.getName()));
            final Class<?> dataType = BuilderUtil.getDataType(element.getType());
            final String[] replaceList = getReplaceList(dataType, className, elementName, element.getName(), enumType);
            clazz.direct(element.asClassComment());
            clazz.direct("    // generated by EnumBuilder:" + dataType);
            for (String methodBody : METHOD_LIST) {
                clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
            }
        }
    }

    private String[] getReplaceList(final Class<?> dataType, final String className, final String elementNameCamelCase, final String elementName, final MetadataEnum enumType) {
        return new String[] {dataType.getSimpleName(), CodeGen.getPascalizeCase(elementName), elementNameCamelCase, className, elementName, enumType.getPackageApi() + "." + CodeGen.getPascalizeCase(enumType.getName())};
    }

}

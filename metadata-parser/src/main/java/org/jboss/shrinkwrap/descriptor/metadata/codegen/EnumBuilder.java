package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataEnum;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataItem;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class EnumBuilder {
    private static final Logger log = Logger.getLogger(EnumBuilder.class.getName());
    private static final String[] SEARCH_LIST = new String[] {"DATATYPE", "ELEMENTNAME_P", "ELEMENTNAME_C", "CLASSNAME_P", "ELEMENTNAME_O", "ELEMENTTYPE_P"};

    private static final String SET_AS_ENUM_TYPE = "\n"
        + "    /**\n"
        + "     * Sets the <code>ELEMENTNAME_O</code> element\n"
        + "     * @param ELEMENTNAME_C the value for the element <code>ELEMENTNAME_O</code> \n"
        + "     * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "     */\n"
        + "    public CLASSNAME_P ELEMENTNAME_C(ELEMENTTYPE_P ELEMENTNAME_C) {\n"
        + "        getNode().getOrCreate(\"ELEMENTNAME_O\").text(ELEMENTNAME_C);\n"
        + "        return this;\n"
        + "    }\n";

    private static final String GET_FROM_STRING_VALUE = "\n"
        + "    public static ELEMENTTYPE_P getFromStringValue(String value) {\n"
        + "        for (ELEMENTTYPE_P type : ELEMENTTYPE_P.values()) {\n"
        + "            if (value != null && type.toString().equals(value)) {\n"
        + "                return type;\n"
        + "            }\n"
        + "        }\n"
        + "        return null;\n"
        + "    }\n";

//    private static final String SET_AS_STRING_TYPE = "\n"
//               + "    /**\n"
//               + "    * Sets the <code>credential-interface</code> element\n"
//        + "    * @param credentialInterface the value for the element <code>credential-interface</code> \n"
//       + "    * @return the current instance of <code>AuthenticationMechanismType</code> \n"
//        + "    */\n"
//       + "    public AuthenticationMechanismType credentialInterface(String credentialInterface) {\n"
//        + "      getNode().getOrCreate("credential-interface").text(credentialInterface);\n"
//        + "      return this;\n"
//        + "    }\n";
//
//    private static final String GET_AS_ENUM_TYPE = "\n"
//               + "    /**\n"
//               + "     * Returns the <code>credential-interface</code> element\n"
//       + "     * @return the value found for the element <code>credential-interface</code> \n"
//       + "    */\n"
//       + "    public CredentialInterfaceType getCredentialInterface() {\n"
//       + "    return CredentialInterfaceType.getFromStringValue(getNode().getTextValueForPatternName("credential-interface"));\n"
//       + "    }\n";
//
//    private static final String GET_AS_ENUM_STRING = "\n"
//               + "    /**\n"
//               + "    * Returns the <code>credential-interface</code> element\n"
//        + "     * @return the value found for the element <code>credential-interface</code> \n"
//        + "    */\n"
//        + "    public String  getCredentialInterfaceAsString() {\n"
//        + "       return getNode().getTextValueForPatternName("credential-interface");\n"
//        + "    }\n";
//
//    private static final String REM_AS_ENUM_TYPE = "\n"
//               + "    /**\n"
//               + "     * Removes the <code>credential-interface</code> attribute \n"
//       + "    * @return the current instance of <code>AuthenticationMechanismType</code> \n"
//        + "    */\n"
//         + "    public AuthenticationMechanismType removeCredentialInterface() {\n"
//        + "     getNode().removeAttribute("credential-interface");\n"
//        + "      return this;\n"
//        + "     }\n";
//
//    private static final String GET_WITH_DATATYPE = "\n"
//        + "    /**\n"
//        + "     * Returns the <code>ELEMENTNAME_C</code> attribute\n"
//        + "     * @return the value defined for the attribute <code>ELEMENTNAME_C</code>\n"
//        + "     */\n"
//        + "    public DATATYPE getELEMENTNAME_P() {\n"
//        + "        if (getNode().getAttribute(\"ELEMENTNAME_C\") != null && !getNode().getAttribute(\"ELEMENTNAME_C\").equals(\"null\")) {\n"
//        + "            return DATATYPE.valueOf(getNode().getAttribute(\"ELEMENTNAME_C\"));\n"
//        + "        }\n"
//        + "        return null;\n"
//        + "    }\n";
//
//    private static final String GET_WITH_BOOLEAN = "\n"
//        + "    /**\n"
//        + "     * Returns the <code>ELEMENTNAME_C</code> attribute\n"
//        + "     * @return the value defined for the attribute <code>ELEMENTNAME_C</code>\n"
//        + "     */\n"
//        + "    public Boolean isELEMENTNAME_P() {\n"
//        + "        return org.jboss.shrinkwrap.descriptor.impl.base.Strings.isTrue(getNode().getAttribute(\"ELEMENTNAME_C\"));\n"
//        + "    }\n";
//
//    private static final String GET_WITH_STRING = "\n"
//        + "    /**\n"
//        + "     * Returns the <code>name</code> attribute\n"
//        + "     * @return the value defined for the attribute <code>ELEMENTNAME_C</code>\n"
//        + "     */\n"
//        + "    public String getELEMENTNAME_P() {\n"
//        + "        return getNode().getAttribute(\"ELEMENTNAME_C\");\n"
//        + "    }\n";
//
//    private static final String SET_WITH_DATATYPE = "\n"
//        + "    /**\n"
//        + "     * Sets the <code>ELEMENTNAME_C</code> attribute\n"
//        + "     * @param ELEMENTNAME_C the value for the attribute <code>ELEMENTNAME_C</code>\n"
//        + "     * @return the current instance of <code>CLASSNAME_P</code>\n"
//        + "     */\n"
//        + "    public CLASSNAME_P ELEMENTNAME_C(DATATYPE ELEMENTNAME_C) {\n"
//        + "        getNode().attribute(\"ELEMENTNAME_C\", ELEMENTNAME_C);\n"
//        + "        return this;\n"
//        + "    }\n";
//
//    private static final String REM_WITH_DATATYPE = "\n"
//        + "    /**\n"
//        + "     * Removes the <code>ELEMENTNAME_C</code> attribute\n"
//        + "     * @return the current instance of <code>CLASSNAME_P</code>\n"
//        + "     */\n"
//        + "    public CLASSNAME_P removeELEMENTNAME_P() {\n"
//        + "        getNode().removeAttribute(\"ELEMENTNAME_C\");\n"
//        + "        return this;\n"
//        + "    }\n";

    public static void addMethods(final JDefinedClass clazz, final Metadata metadata, final MetadataItem metadataClass, final String className, final boolean isApi) throws Exception {
        generate(clazz, className, metadata, metadataClass, isApi);
    }

    /**
     *
   public String toString() {return value;}

   public static EnvEntryTypeValuesType getFromStringValue(String value)
   {
      for(EnvEntryTypeValuesType type: EnvEntryTypeValuesType.values())
      {
         if(value != null && type.toString().equals(value))
        { return type;}
      }
      return null;
   }

     * @param metadata
     * @param path
     * @throws Exception
     */
    public static void createEnums(final Metadata metadata, final String path) throws Exception {
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

            enumClass.direct(BuilderUtil.replaceAll(GET_FROM_STRING_VALUE, false, new String[] {"ELEMENTTYPE_P"}, new String[] {className}));
//
//            final JMethod getFromMethod = enumClass.method(JMod.PUBLIC | JMod.STATIC, enumClass, "getFromStringValue");
//            getFromMethod.

            final File file = new File(path);
            jcm.build(file);
        }
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private static MetadataEnum getEnum(final Metadata metadata, final MetadataElement element) {
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

    private static void generate(final JDefinedClass clazz, final String className, final Metadata metadata, final MetadataItem metadataClass, final boolean isApi) throws Exception {
        for (final MetadataElement element : metadataClass.getElements()) {
            final MetadataEnum enumType = getEnum(metadata, element);
            if (enumType != null) {
                final String elementName = BuilderUtil.checkReservedWords(CodeGen.getCamelCase(element.getName()));
                final Class<?> dataType = BuilderUtil.getDataType(element.getType());
                final String[] replaceList = getReplaceList(dataType, className, elementName, element.getName(), enumType);
                addConstants(clazz, metadataClass, isApi);
                clazz.direct(element.asClassComment());
                clazz.direct(BuilderUtil.replaceAll(SET_AS_ENUM_TYPE, isApi, SEARCH_LIST, replaceList));
            }
        }
    }

    private static String[] getReplaceList(final Class<?> dataType, final String className, final String elementNameCamelCase, final String elementName, final MetadataEnum enumType) {
        return new String[] {dataType.getSimpleName(), CodeGen.getPascalizeCase(elementName), elementNameCamelCase, className, elementName, enumType.getPackageApi() + "." + CodeGen.getPascalizeCase(enumType.getName())};
    }

    private static void addConstants(final JDefinedClass clazz, final MetadataItem metadataClass, final boolean isApi) {
        if (isApi) {
            for (final MetadataElement element : metadataClass.getElements()) {
                final List<String> constants = new ArrayList<String>();
                if (element.getFixedValue() != null && !element.getFixedValue().isEmpty()) {
                    final String elementName = BuilderUtil.checkReservedWords(CodeGen.getCamelCase(element.getName()));
                    final Class<?> dataType = BuilderUtil.getDataType(element.getType());
                    if (!constants.contains(elementName)) {
                        constants.add(elementName);
                        System.out.println(metadataClass.getName() + ":" + element.getName() + ":" + element.getFixedValue());
//                        if (dataType == Integer.class) {
//                            clazz.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, Integer.class, elementName.toUpperCase(), JExpr.direct(element.getFixedValue()));
//                        } else if (dataType == Boolean.class) {
//                            clazz.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, Boolean.class, elementName.toUpperCase(), JExpr.direct(element.getFixedValue()));
//                        } else {
//                            clazz.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, elementName.toUpperCase(), JExpr.direct(element.getFixedValue()));
//                        }
                    }
                }
            }
        }
    }

}

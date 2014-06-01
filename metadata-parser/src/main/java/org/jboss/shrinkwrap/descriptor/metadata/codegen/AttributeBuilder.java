package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.util.ArrayList;
import java.util.List;

import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataItem;

import com.sun.codemodel.JDefinedClass;

public class AttributeBuilder {
    private static final String[] SEARCH_LIST = new String[] {"DATATYPE", "ELEMENTNAME_P", "ELEMENTNAME_C", "CLASSNAME_P"};

    private static final String GET_WITH_DATATYPE = "\n"
        + "    /**\n"
        + "     * Returns the <code>ELEMENTNAME_C</code> attribute\n"
        + "     * @return the value defined for the attribute <code>ELEMENTNAME_C</code>\n"
        + "     */\n"
        + "    public DATATYPE getELEMENTNAME_P() {\n"
        + "        if (getNode().getAttribute(\"ELEMENTNAME_C\") != null && !getNode().getAttribute(\"ELEMENTNAME_C\").equals(\"null\")) {\n"
        + "            return DATATYPE.valueOf(getNode().getAttribute(\"ELEMENTNAME_C\"));\n"
        + "        }\n"
        + "        return null;\n"
        + "    }\n";

    private static final String GET_WITH_BOOLEAN = "\n"
        + "    /**\n"
        + "     * Returns the <code>ELEMENTNAME_C</code> attribute\n"
        + "     * @return the value defined for the attribute <code>ELEMENTNAME_C</code>\n"
        + "     */\n"
        + "    public Boolean isELEMENTNAME_P() {\n"
        + "        return org.jboss.shrinkwrap.descriptor.impl.base.Strings.isTrue(getNode().getAttribute(\"ELEMENTNAME_C\"));\n"
        + "    }\n";

    private static final String GET_WITH_STRING = "\n"
        + "    /**\n"
        + "     * Returns the <code>name</code> attribute\n"
        + "     * @return the value defined for the attribute <code>ELEMENTNAME_C</code>\n"
        + "     */\n"
        + "    public String getELEMENTNAME_P() {\n"
        + "        return getNode().getAttribute(\"ELEMENTNAME_C\");\n"
        + "    }\n";

    private static final String SET_WITH_DATATYPE = "\n"
        + "    /**\n"
        + "     * Sets the <code>ELEMENTNAME_C</code> attribute\n"
        + "     * @param ELEMENTNAME_C the value for the attribute <code>ELEMENTNAME_C</code>\n"
        + "     * @return the current instance of <code>CLASSNAME_P</code>\n"
        + "     */\n"
        + "    public CLASSNAME_P ELEMENTNAME_C(DATATYPE ELEMENTNAME_C) {\n"
        + "        getNode().attribute(\"ELEMENTNAME_C\", ELEMENTNAME_C);\n"
        + "        return this;\n"
        + "    }\n";

    private static final String REM_WITH_DATATYPE = "\n"
        + "    /**\n"
        + "     * Removes the <code>ELEMENTNAME_C</code> attribute\n"
        + "     * @return the current instance of <code>CLASSNAME_P</code>\n"
        + "     */\n"
        + "    public CLASSNAME_P removeELEMENTNAME_P() {\n"
        + "        getNode().removeAttribute(\"ELEMENTNAME_C\");\n"
        + "        return this;\n"
        + "    }\n";

    public static void addMethods(final JDefinedClass clazz, final MetadataItem metadataClass, final String className, final boolean isApi) throws Exception {
        generateImpl(clazz, className, metadataClass, isApi);
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private static void generateImpl(final JDefinedClass clazz, final String className, final MetadataItem metadataClass, final boolean isApi) throws Exception {
        for (final MetadataElement element : metadataClass.getElements()) {
            if (element.getIsAttribute()) {
                final String elementName = BuilderUtil.checkReservedWords(CodeGen.getCamelCase(element.getName()));
                final Class<?> dataType = BuilderUtil.getDataType(element.getType());
                final String[] replaceList = getReplaceList(dataType, className, elementName);
                addConstants(clazz, metadataClass, isApi);
                clazz.direct(element.asClassComment());
                if (dataType == Integer.class) {
                    clazz.direct(BuilderUtil.replaceAll(GET_WITH_DATATYPE, isApi, SEARCH_LIST, replaceList));
                    clazz.direct(BuilderUtil.replaceAll(SET_WITH_DATATYPE, isApi, SEARCH_LIST, replaceList));
                    clazz.direct(BuilderUtil.replaceAll(REM_WITH_DATATYPE, isApi, SEARCH_LIST, replaceList));
                } else if (dataType == Boolean.class) {
                    clazz.direct(BuilderUtil.replaceAll(GET_WITH_BOOLEAN, isApi, SEARCH_LIST, replaceList));
                    clazz.direct(BuilderUtil.replaceAll(SET_WITH_DATATYPE, isApi, SEARCH_LIST, replaceList));
                    clazz.direct(BuilderUtil.replaceAll(REM_WITH_DATATYPE, isApi, SEARCH_LIST, replaceList));
                } else {
                    clazz.direct(BuilderUtil.replaceAll(GET_WITH_STRING, isApi, SEARCH_LIST, replaceList));
                    clazz.direct(BuilderUtil.replaceAll(SET_WITH_DATATYPE, isApi, SEARCH_LIST, replaceList));
                    clazz.direct(BuilderUtil.replaceAll(REM_WITH_DATATYPE, isApi, SEARCH_LIST, replaceList));
                }
            }
        }
    }

    private static String[] getReplaceList(final Class<?> dataType, final String className, final String elementName) {
        return new String[] {dataType.getSimpleName(), CodeGen.getPascalizeCase(elementName), elementName, className};
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

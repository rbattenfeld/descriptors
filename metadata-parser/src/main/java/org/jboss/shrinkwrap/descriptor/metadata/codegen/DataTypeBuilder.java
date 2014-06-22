package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;

public class DataTypeBuilder implements MethodGeneratorContract {

    private static final String[] SEARCH_LIST = new String[] { "DATATYPE", "ELEMENTNAME_P", "ELEMENTNAME_C", "CLASSNAME_P", "ELEMENTNAME_O" };

    private static final String SET_DATATYPE_UNBOUNDED = "\n"
        + "     /**\n"
        + "      * Creates for all DATATYPE objects representing <code>ELEMENTNAME_O</code> elements, \n"
        + "      * a new <code>ELEMENTNAME_O</code> element \n"
        + "      * @param values list of <code>ELEMENTNAME_O</code> objects \n"
        + "      * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "      */\n"
        + "     public CLASSNAME_P ELEMENTNAME_C(DATATYPE ... values) {\n"
        + "        if (values != null) {\n"
        + "            for (String name: values) {\n"
        + "               getNode().createChild(\"ELEMENTNAME_O\").text(name);\n"
        + "            }\n"
        + "        }\n"
        + "        return this;\n"
        + "     }\n";

    private static final String GET_ALL_DATATYPE_UNBOUNDED = "\n"
        + "     /**\n"
        + "      * Returns all <code>ELEMENTNAME_O</code> elements\n"
        + "      * @return list of <code>ELEMENTNAME_O</code> \n"
        + "      */\n"
        + "     public java.util.List<DATATYPE> getAllELEMENTNAME_P() {\n"
        + "         java.util.List<DATATYPE> result = new java.util.ArrayList<DATATYPE>();\n"
        + "         java.util.List<Node> nodes = getNode().get(\"ELEMENTNAME_O\");\n"
        + "         for (Node node : nodes) {\n"
        + "             result.add(node.getText());\n"
        + "         }\n"
        + "         return result;\n"
        + "     }\n";

    private static final String REM_ALL_DATATYPE_UNBOUNDED = "\n"
        + "     /**\n"
        + "      * Removes the <code>ELEMENTNAME_O</code> element \n"
        + "      * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "      */\n"
        + "     public CLASSNAME_P removeAllELEMENTNAME_P() {\n"
        + "         getNode().removeChildren(\"ELEMENTNAME_O\");\n"
        + "         return this;\n"
        + "     }\n";

    private static final String SET_DATATYPE_SINGLE = "\n"
        + "     /**\n"
        + "      * Sets the <code>ELEMENTNAME_O</code> element\n"
        + "      * @param valueClass the value for the element <code>ELEMENTNAME_O</code> \n"
        + "      * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "      */\n"
        + "     public CLASSNAME_P ELEMENTNAME_C(DATATYPE value) {\n"
        + "         getNode().getOrCreate(\"ELEMENTNAME_O\").text(value);\n"
        + "         return this;\n"
        + "     }\n";

    private static final String GET_DATATYPE_SINGLE = "\n"
        + "     /**\n"
        + "      * Returns the <code>ELEMENTNAME_O</code> element\n"
        + "      * @return the node defined for the element <code>ELEMENTNAME_O</code> \n"
        + "      */\n"
        + "     public DATATYPE getELEMENTNAME_P() {\n"
        + "         return getNode().getTextValueForPatternName(\"ELEMENTNAME_O\");\n"
        + "     }\n";

    private static final String REM_DATATYPE_SINGLE = "\n"
        + "     /**\n"
        + "      * Removes the <code>ELEMENTNAME_O</code> element\n"
        + "      * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "      */\n"
        + "     public CLASSNAME_P removeELEMENTNAME_P() {\n"
        + "         getNode().removeChildren(\"ELEMENTNAME_O\");\n"
        + "         return this;\n"
        + "     }\n";

    private static final String SET_DATATYPE_DATE_SINGLE = "\n"
        + "     /**\n"
        + "      * Sets the <code>ELEMENTNAME_O</code> element\n"
        + "      * @param start the value for the element <code>ELEMENTNAME_O</code> \n"
        + "      * @return the current instance of <code>CLASSNAME_P</code> \n"
        + "      */\n"
        + "     public CLASSNAME_P ELEMENTNAME_C(java.util.Date ELEMENTNAME_C) {\n"
        + "         if (ELEMENTNAME_C != null) {\n"
        + "             getNode().getOrCreate(\"ELEMENTNAME_O\").text(org.jboss.shrinkwrap.descriptor.impl.base.XMLDate.toXMLFormat(ELEMENTNAME_C));\n"
        + "             return this;\n"
        + "         }\n"
        + "         return null;\n"
        + "     }\n";

    private static final String GET_DATATYPE_DATE_SINGLE = "\n"
        + "     /**\n"
        + "      * Returns the <code>ELEMENTNAME_O</code> element\n"
        + "      * @return the node defined for the element <code>ELEMENTNAME_O</code> \n"
        + "      */\n"
        + "     public java.util.Date getELEMENTNAME_P() {\n"
        + "         if (getNode().getTextValueForPatternName(\"ELEMENTNAME_C\") != null) {\n"
        + "             return org.jboss.shrinkwrap.descriptor.impl.base.XMLDate.toDate(getNode().getTextValueForPatternName(\"ELEMENTNAME_O\"));\n"
        + "         }\n"
        + "         return null;\n"
        + "     }\n";

    private static final String GET_AS_BOOLEAN = "\n"
        + "    /**\n"
        + "     * Returns the <code>ELEMENTNAME_O</code> attribute\n"
        + "     * @return the value defined for the attribute <code>ELEMENTNAME_O</code>\n"
        + "     */\n"
        + "    public Boolean isELEMENTNAME_P() {\n"
        + "        return org.jboss.shrinkwrap.descriptor.impl.base.Strings.isTrue(getNode().getAttribute(\"ELEMENTNAME_O\"));\n"
        + "    }\n";

    private static final String GET_AS_INTEGER = "\n"
        + "    /**\n"
        + "     * Returns the <code>ELEMENTNAME_O</code> element\n"
        + "     * @return the node defined for the element <code>ELEMENTNAME_O</code> \n"
        + "     */\n"
        + "    public Integer getELEMENTNAME_P() {\n"
        + "        if (getNode().getTextValueForPatternName(\"ELEMENTNAME_O\") != null && !getNode().getTextValueForPatternName(\"ELEMENTNAME_O\").equals(\"null\")) {\n"
        + "            return Integer.valueOf(getNode().getTextValueForPatternName(\"ELEMENTNAME_O\"));\n"
        + "        }\n"
        + "        return null;\n"
        + "    }\n";

    private static final String GET_AS_LONG = "\n"
        + "    /**\n"
        + "     * Returns the <code>ELEMENTNAME_O</code> element\n"
        + "     * @return the node defined for the element <code>ELEMENTNAME_O</code> \n"
        + "     */\n"
        + "    public Long getELEMENTNAME_P() {\n"
        + "        if (getNode().getTextValueForPatternName(\"ELEMENTNAME_O\") != null && !getNode().getTextValueForPatternName(\"ELEMENTNAME_O\").equals(\"null\")) {\n"
        + "            return Long.valueOf(getNode().getTextValueForPatternName(\"ELEMENTNAME_O\"));\n"
        + "        }\n"
        + "        return null;\n"
        + "    }\n";

    private static final String[] METHOD_LIST_DATATYPE_UNBOUNDED = new String[] {
        SET_DATATYPE_UNBOUNDED,
        GET_ALL_DATATYPE_UNBOUNDED,
        REM_ALL_DATATYPE_UNBOUNDED,
    };

    private static final String[] METHOD_LIST_DATATYPE_SINGLE = new String[] {
        SET_DATATYPE_SINGLE,
        GET_DATATYPE_SINGLE,
        REM_DATATYPE_SINGLE,
    };

    private static final String[] METHOD_LIST_DATE_SINGLE = new String[] {
        SET_DATATYPE_DATE_SINGLE,
        GET_DATATYPE_DATE_SINGLE,
        REM_DATATYPE_SINGLE,
    };

    private static final String[] METHOD_LIST_BOOLEAN_SINGLE = new String[] {
        SET_DATATYPE_SINGLE,
        GET_AS_BOOLEAN,
        REM_DATATYPE_SINGLE,
    };

    private static final String[] METHOD_LIST_INTEGER_SINGLE = new String[] {
        SET_DATATYPE_SINGLE,
        GET_AS_INTEGER,
        REM_DATATYPE_SINGLE,
    };

    private static final String[] METHOD_LIST_LONG_SINGLE = new String[] {
        SET_DATATYPE_SINGLE,
        GET_AS_LONG,
        REM_DATATYPE_SINGLE,
    };

    @Override
    public boolean addMethods(final JDefinedClass clazz, final  Metadata metadata, final MetadataElement element, final String className, final boolean isApi) throws Exception {
        if (BuilderUtil.isDataType(metadata, element)) {
            generateImpl(clazz, className, metadata, element, isApi);
            return true;
        }
        return false;
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private static void generateImpl(final JDefinedClass clazz, final String className, final Metadata metadata, final MetadataElement element, final boolean isApi) throws Exception {
        final String elementName_c = BuilderUtil.checkReservedWords(CodeGen.getCamelCase(element.getName()));
        final Class<?> dataType = BuilderUtil.getDataType(element.getType());
        final String[] replaceList = getReplaceList(dataType, className, elementName_c, element.getName());
        clazz.direct(element.asClassComment());
        clazz.direct("    // generated by DataTypeBuilder:" + dataType);
        if ("unbounded".equals(element.getMaxOccurs())) {
            for (String methodBody : METHOD_LIST_DATATYPE_UNBOUNDED) {
                clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
            }
            final JFieldVar fixedAttribute = BuilderUtil.checkFixedValue(clazz, element, elementName_c, String.class);
            if (fixedAttribute != null) {
                fixedAttribute.init(JExpr.lit(element.getFixedValue()));
            }
        } else {
            if (dataType == Boolean.class) {
                for (String methodBody : METHOD_LIST_BOOLEAN_SINGLE) {
                    clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
                }
                final JFieldVar fixedAttribute = BuilderUtil.checkFixedValue(clazz, element, elementName_c, Boolean.class);
                if (fixedAttribute != null) {
                    fixedAttribute.init(JExpr.lit(Boolean.parseBoolean(element.getFixedValue())));
                }
            } else if (dataType == Integer.class) {
                for (String methodBody : METHOD_LIST_INTEGER_SINGLE) {
                    clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
                }
                final JFieldVar fixedAttribute = BuilderUtil.checkFixedValue(clazz, element, elementName_c, Integer.class);
                if (fixedAttribute != null) {
                    fixedAttribute.init(JExpr.lit(Integer.parseInt(element.getFixedValue())));
                }
            } else if (dataType == Long.class) {
                for (String methodBody : METHOD_LIST_LONG_SINGLE) {
                    clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
                }
                final JFieldVar fixedAttribute = BuilderUtil.checkFixedValue(clazz, element, elementName_c, Long.class);
                if (fixedAttribute != null) {
                    fixedAttribute.init(JExpr.lit(Long.parseLong(element.getFixedValue())));
                }
            } else if (dataType == java.util.Date.class) {
                for (String methodBody : METHOD_LIST_DATE_SINGLE) {
                    clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
                }
            } else {
                for (String methodBody : METHOD_LIST_DATATYPE_SINGLE) {
                    clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
                }
                final JFieldVar fixedAttribute = BuilderUtil.checkFixedValue(clazz, element, elementName_c, String.class);
                if (fixedAttribute != null) {
                    fixedAttribute.init(JExpr.lit(element.getFixedValue()));
                }
            }
        }
    }

    private static String[] getReplaceList(final Class<?> dataType, final String className, final String elementName_c, final String elementName) {
        return new String[] {dataType.getSimpleName(), CodeGen.getPascalizeCase(elementName_c), elementName_c, className, elementName};
    }

}

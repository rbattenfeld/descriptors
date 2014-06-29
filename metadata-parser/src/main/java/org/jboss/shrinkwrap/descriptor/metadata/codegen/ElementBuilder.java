package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataItem;

import com.sun.codemodel.JDefinedClass;

public class ElementBuilder implements MethodGeneratorContract {

    private static final String[] SEARCH_LIST = new String[] { "DATATYPE", "ELEMENTNAME_P", "ELEMENTNAME_C", "CLASSNAME_P", "ELEMENTNAME_O", "ELEMENTTYPE_P", "ELEMENTTYPE_IMPL_P" };

    //-----------------------------------------------------------------------||
    //-- Single Elements ----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private static final String METHOD_SET_SINGLE_ELEMENT = "\n"
       + "    /**\n"
       + "     * If not already created, a new <code>ELEMENTNAME_O</code> element with the given value will be created.\n"
       + "     * Otherwise, the existing <code>ELEMENTNAME_O</code> element will be returned.\n"
       + "     * @return  a new or existing instance of <code>CLASSNAME_P</code> \n"
       + "     */\n"
       + "    public CLASSNAME_P ELEMENTNAME_C(final ELEMENTTYPE_P value) {\n"
       + "        if (value instanceof ChildNodeInitializer) {\n"
       + "            if (getNode().getSingle(\"ELEMENTNAME_O\") == null) {\n"
       + "                ((ChildNodeInitializer)value).initialize(\"ELEMENTNAME_O\", getNode());\n"
       + "                org.jboss.shrinkwrap.descriptor.impl.base.extension.ExtensionProvider.handleSet(this, getNode(), \"ELEMENTNAME_O\", value);\n"
       + "            } else {\n"
       + "                throw new IllegalArgumentException(\"Single child already assigned\");\n"
       + "            }\n"
       + "        }\n"
       + "        return this;\n"
       + "    }\n";

    private static final String METHOD_REM_SINGLE_ELEMENT = "\n"
       + "    /**\n"
       + "     * Removes the <code>ELEMENTNAME_O</code> element \n"
       + "     * @return the current instance of <code>CLASSNAME_P</code> \n"
       + "     */\n"
       + "    public CLASSNAME_P removeELEMENTNAME_P() {\n"
       + "        getNode().removeChildren(\"ELEMENTNAME_O\");\n"
       + "        org.jboss.shrinkwrap.descriptor.impl.base.extension.ExtensionProvider.handleRemove(this, getNode(), \"ELEMENTNAME_O\");\n"
       + "        return this;\n"
       + "    }\n";

    private static final String METHOD_SET_EMTPY_BOOLEAN_ELEMENT = "\n"
       + "    /**\n"
       + "     * Sets the <code>ELEMENTNAME_O</code> element\n"
       + "     * @return the current instance of <code>CLASSNAME_P</code>\n"
       + "     */\n"
       + "    public CLASSNAME_P ELEMENTNAME_C() {\n"
       + "        getNode().getOrCreate(\"ELEMENTNAME_O\");\n"
       + "        org.jboss.shrinkwrap.descriptor.impl.base.extension.ExtensionProvider.handleSet(this, getNode(), \"ELEMENTNAME_O\", null);\n"
       + "        return this;\n"
       + "    }\n";

    private static final String METHOD_GET_EMTPY_BOOLEAN_ELEMENT = "\n"
       + "     /**\n"
       + "      * Removes the <code>ELEMENTNAME_O</code> element\n"
       + "      * @return the current instance of <code>EjbRelationshipRoleType</code> \n"
       + "      */\n"
       + "      public Boolean isELEMENTNAME_P() {\n"
       + "          return getNode().getSingle(\"ELEMENTNAME_O\") != null;\n"
       + "      }\n";

    //-----------------------------------------------------------------------||
    //-- Unbounded Elements -------------------------------------------------||
    //-----------------------------------------------------------------------||

    private static final String METHOD_ADD_UNBOUNDED_ELEMENT = "\n"
       + "    /**\n"
       + "     * Adds a new <code>ELEMENTNAME_O</code> element \n"
       + "     * @return the new created instance of <code>ELEMENTNAME_O</code> \n"
       + "     */\n"
       + "    public CLASSNAME_P addELEMENTNAME_P(final ELEMENTTYPE_P value) {\n"
       + "        if (value instanceof ChildNodeInitializer) {\n"
       + "           ((ChildNodeInitializer)value).initialize(\"ELEMENTNAME_O\", getNode());\n"
       + "           org.jboss.shrinkwrap.descriptor.impl.base.extension.ExtensionProvider.handleSet(this, getNode(), \"ELEMENTNAME_O\", value);\n"
       + "        }\n"
       + "        return this;\n"
       + "    }\n";

    private static final String METHOD_GET_ALL_UNBOUNDED_ELEMENTS = "\n"
       + "    /**\n"
       + "     * Returns all <code>ELEMENTNAME_O</code> elements\n"
       + "     * @return list of <code>ELEMENTNAME_O</code> \n"
       + "     */\n"
       + "    public java.util.List<ELEMENTTYPE_P> getAllELEMENTNAME_P() {\n"
       + "        java.util.List<ELEMENTTYPE_P> list = new java.util.ArrayList<ELEMENTTYPE_P>();\n"
       + "        java.util.List<Node> nodeList = getNode().get(\"ELEMENTNAME_O\");\n"
       + "        for (Node node: nodeList) {\n"
       + "            ELEMENTTYPE_P  type = new ELEMENTTYPE_IMPL_PImpl(\"ELEMENTNAME_O\", getNode(), node);\n"
       + "            list.add(type);\n"
       + "         }\n"
       + "         return list;\n"
       + "    }\n";

    private static final String METHOD_REM_ALL_UNBOUNDED_ELEMENTS = "\n"
       + "    /**\n"
       + "     * Removes all <code>ELEMENTNAME_O</code> elements \n"
       + "     * @return the current instance of <code>ELEMENTNAME_O</code> \n"
       + "     */\n"
       + "    public CLASSNAME_P removeAllELEMENTNAME_P() {\n"
       + "        getNode().removeChildren(\"ELEMENTNAME_O\");\n"
       + "        org.jboss.shrinkwrap.descriptor.impl.base.extension.ExtensionProvider.handleRemove(this, getNode(), \"ELEMENTNAME_O\");\n"
       + "        return this;\n"
       + "    }\n";

    private static final String[] METHOD_LIST_SINGLE = new String[] {
        METHOD_SET_SINGLE_ELEMENT,
        METHOD_REM_SINGLE_ELEMENT,
    };

    private static final String[] METHOD_LIST_UNBOUNDED = new String[] {
        METHOD_ADD_UNBOUNDED_ELEMENT,
        METHOD_GET_ALL_UNBOUNDED_ELEMENTS,
        METHOD_REM_ALL_UNBOUNDED_ELEMENTS,
    };

    private static final String[] METHOD_LIST_EMPTY_BOOLEAN = new String[] {
        METHOD_SET_EMTPY_BOOLEAN_ELEMENT,
        METHOD_GET_EMTPY_BOOLEAN_ELEMENT,
        METHOD_REM_SINGLE_ELEMENT,
    };

    @Override
    public boolean addMethods(final JDefinedClass clazz, final  Metadata metadata, final MetadataElement element, final String className, final boolean isApi) throws Exception {
        if ("unbounded".equals(element.getMaxOccurs())) {
            generateUnboundedElementMethods(clazz, className, metadata, element, isApi);
        } else {
            generateSingleElementMethods(clazz, className, metadata, element, isApi);
        }
        return true;
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private void generateSingleElementMethods(final JDefinedClass clazz, final String className, final Metadata metadata, final MetadataElement element, final boolean isApi) throws Exception {
        final MetadataItem classType = BuilderUtil.findClass(metadata, element);
        if (classType != null) {
            final String elementName = BuilderUtil.checkReservedWords(CodeGen.getCamelCase(element.getName()));
            final Class<?> dataType = BuilderUtil.getDataType(element.getType());
            final String[] replaceList = getReplaceList(dataType, className, elementName, element.getName(), element.getType(), classType);
            clazz.direct(element.asClassComment());
            clazz.direct("    // generated by ElementBuilder:" + dataType);
            if (BuilderUtil.isEmptyBooleanType(element.getType())) {
                for (String methodBody : METHOD_LIST_EMPTY_BOOLEAN) {
                    clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
                }
            } else {
                for (String methodBody : METHOD_LIST_SINGLE) {
                    clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
                }
            }
        }
    }

    private void generateUnboundedElementMethods(final JDefinedClass clazz, final String className, final Metadata metadata, final MetadataElement element, final boolean isApi) throws Exception {
        final MetadataItem classType = BuilderUtil.findClass(metadata, element);
        if (classType != null) {
            final String elementName = BuilderUtil.checkReservedWords(CodeGen.getCamelCase(element.getName()));
            final Class<?> dataType = BuilderUtil.getDataType(element.getType());
            final String[] replaceList = getReplaceList(dataType, className, elementName, element.getName(), element.getType(), classType);
            clazz.direct(element.asClassComment());
            clazz.direct("    // generated by ElementBuilder:" + dataType);
            for (String methodBody : METHOD_LIST_UNBOUNDED) {
                clazz.direct(BuilderUtil.replaceAll(methodBody, isApi, SEARCH_LIST, replaceList));
            }
        }
    }

    private String[] getReplaceList(final Class<?> dataType, final String className, final String elementNameCamelCase, final String elementName, final String elementType, final MetadataItem classType) {
        final String[] items = elementType.split(":", -1);
        return new String[] {
            dataType.getSimpleName(),
            CodeGen.getPascalizeCase(elementName),
            elementNameCamelCase,
            className,
            elementName,
            classType.getPackageApi() + "." + CodeGen.getPascalizeCase(items[1]),
            classType.getPackageImpl() + "." + CodeGen.getPascalizeCase(items[1])};
    }

}

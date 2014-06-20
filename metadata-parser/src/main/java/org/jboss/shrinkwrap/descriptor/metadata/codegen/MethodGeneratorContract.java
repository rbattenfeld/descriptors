package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;

import com.sun.codemodel.JDefinedClass;

public interface MethodGeneratorContract {

    /**
     * Adds getter, setter and remove methods.
     * @param clazz the class to be created.
     * @param metadata the overall metadata context. Contains everything; classes, enums, groups...
     * @param element the element which has to be added as getter, setter and remove method to this class.
     * @param className the name of the class.
     * @param isApi true, if the clazz is an interface.
     * @return true, if the context, meaning data type is applicable.
     * @throws Exception
     */
    boolean addMethods(final JDefinedClass clazz, final Metadata metadata, final MetadataElement element, final String className, final boolean isApi) throws Exception;
}

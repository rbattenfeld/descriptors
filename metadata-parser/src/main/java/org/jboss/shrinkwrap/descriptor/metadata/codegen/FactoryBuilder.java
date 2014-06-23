package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

public class FactoryBuilder {

    public void createFactory(final String pathToApi, final String pathToImpl, final String factoryContext) throws Exception {
        final List<File> fileList = getFiles(pathToApi);
        final List<String> factoryClasses = new ArrayList<String>();
        final JavaProjectBuilder builder = new JavaProjectBuilder();
        for (final File file : fileList) {
            if (file.getName().endsWith(".java")) {
                final JavaSource src = builder.addSource(file);
                if (src != null) {
                    final JavaClass clazz = src.getClasses().get(0);
                    if (clazz.isPublic()
                        && !clazz.isEnum()
                        && !isAnnotated(clazz)
                        && !clazz.getName().endsWith("Descriptor")
                        && !clazz.getName().endsWith("Tmp")
                        && !clazz.getFullyQualifiedName().startsWith("org.jboss.shrinkwrap.descriptor.api.Factory")) {
                            factoryClasses.add(clazz.getFullyQualifiedName());
                        }
                }
            }
        }

        createFactoryApi(pathToApi, "org.jboss.shrinkwrap.descriptor.api.Factory" + factoryContext, factoryClasses);
        createFactoryImpl(pathToImpl, "org.jboss.shrinkwrap.descriptor.api.Factory" + factoryContext, "org.jboss.shrinkwrap.descriptor.impl.Factory" +  factoryContext + "Impl", factoryClasses);
    }

    //-----------------------------------------------------------------------||
    //--Private Methods -----------------------------------------------------||
    //-----------------------------------------------------------------------||

    private String getContext(final String pathToApi) {
        final Pattern pattern = Pattern.compile("/api-(.*?)/");
        final java.util.regex.Matcher matcher = pattern.matcher(pathToApi);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private void createFactoryApi(final String pathToApi, final String fullyQualifiedFactoryName, final List<String> factoryClasses) throws Exception {
        final JCodeModel jcm = new JCodeModel();
        final JDefinedClass factoryApiClass = jcm._class(fullyQualifiedFactoryName, ClassType.INTERFACE);

        for (final String factoryClassName : factoryClasses) {
            final JClass factoryChild = jcm.directClass(factoryClassName);
            factoryApiClass.method(JMod.PUBLIC, factoryChild, getUniqueNameFromQualifiedName(factoryClassName));
        }

        final File file = new File(pathToApi);
        jcm.build(file);
    }

    private void createFactoryImpl(final String pathToImpl, final String fullyQualifiedApiFactoryName, final String fullyQualifiedImplFactoryName, final List<String> factoryClasses) throws Exception {
        final JCodeModel jcm = new JCodeModel();
        final JDefinedClass factoryImplClass = jcm._class(fullyQualifiedImplFactoryName, ClassType.CLASS);
        final JClass factoryAPI = jcm.directClass(fullyQualifiedApiFactoryName);

        factoryImplClass._implements(factoryAPI);
        final JMethod methodInstance = factoryImplClass.method(JMod.STATIC | JMod.PUBLIC, factoryAPI, "instance");
        methodInstance.body()._return(JExpr.direct(String.format("new %s()", fullyQualifiedImplFactoryName)));

        for (final String factoryClassName : factoryClasses) {
            final JClass factoryChild = jcm.directClass(factoryClassName);
            final JMethod method = factoryImplClass.method(JMod.PUBLIC, factoryChild, getUniqueNameFromQualifiedName(factoryClassName));
            method.body()._return(JExpr.direct(String.format("new %sImpl()", factoryClassName.replace("api", "impl"))));
        }

        final File file = new File(pathToImpl);
        jcm.build(file);
    }

    private String getUniqueNameFromQualifiedName(final String factoryClassName) {
        final String[] items = factoryClassName.split("\\.", -1);
        if (items.length >= 2) {
            final String uniqueName = items[items.length - 1] + CodeGen.getPascalizeCase(items[items.length - 2]);
            return CodeGen.getCamelCase(uniqueName);
        } else {
            return CodeGen.getCamelCase(factoryClassName);
        }
    }

    private List<File> getFiles(final String pathToApi) {
        final List<File> fileList = new ArrayList<File>();
        listFiles(fileList, pathToApi);
        return fileList;
    }

    private void listFiles(final List<File> list, final String directoryName) {
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

    private boolean isAnnotated(final JavaClass clazz) {
        final List<JavaAnnotation> annotationList = clazz.getAnnotations();
        if (annotationList != null) {
            for (JavaAnnotation annotation : annotationList) {
                return true;
            }
        }
        return false;
    }
}

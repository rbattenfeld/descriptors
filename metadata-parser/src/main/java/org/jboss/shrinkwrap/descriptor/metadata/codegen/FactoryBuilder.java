package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
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

    private final Pattern p = Pattern.compile("\\d+");

    public void createFactory(final String pathToApi, final String pathToImpl, final String factoryContext) throws Exception {
        final List<File> fileList = getFiles(pathToApi);
        fileList.addAll(getFiles(pathToApi.replace("/main/", "/ext/"))); // hack, I know
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

    private void createFactoryApi(final String pathToApi, final String fullyQualifiedFactoryName, final List<String> factoryClasses) throws Exception {
        final JCodeModel jcm = new JCodeModel();
        final JDefinedClass factoryApiClass = jcm._class(fullyQualifiedFactoryName, ClassType.INTERFACE);
        final List<String> methodNames = new LinkedList<String>();
        for (final String factoryClassName : factoryClasses) {
//            if (factoryClassName.indexOf(".ext.") > 0) {
//                final String ff = factoryClassName.replace(".ext.", ".");
//                final String fff = ff.replace("Ext", "");
//                final JClass factoryChild = jcm.directClass(fff);
//                factoryApiClass.method(JMod.PUBLIC, factoryChild, getUniqueNameFromQualifiedName(factoryClassName));
//            } else {
            final String methodName = getUniqueName(methodNames, factoryClassName);
            final JClass factoryChild = jcm.directClass(factoryClassName);
            factoryApiClass.method(JMod.PUBLIC, factoryChild, methodName);
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

        final List<String> methodNames = new LinkedList<String>();
        for (final String factoryClassName : factoryClasses) {
//            if (factoryClassName.indexOf(".ext.") > 0) {
//                final String ff = factoryClassName.replace(".ext.", ".");
//                final String fff = ff.replace("Ext", "");
//                final JClass factoryChild = jcm.directClass(fff);
//                final JMethod method = factoryImplClass.method(JMod.PUBLIC, factoryChild, getUniqueNameFromQualifiedName(factoryClassName));
//                method.body()._return(JExpr.direct(String.format("new %sImpl()", factoryClassName.replace("api", "impl"))));
//            } else {
            final String methodName = getUniqueName(methodNames, factoryClassName);
            final JClass factoryChild = jcm.directClass(factoryClassName);
            final JMethod method = factoryImplClass.method(JMod.PUBLIC, factoryChild, methodName);
            method.body()._return(JExpr.direct(String.format("new %sImpl()", factoryClassName.replace("api", "impl"))));
        }

        final File file = new File(pathToImpl);
        jcm.build(file);
    }

    private String getUniqueName(final List<String> methodNames, final String factoryClassName) {
         final String methodnameShort = getUniqueNameFromQualifiedName(factoryClassName, true);
         final String methodnameLong = getUniqueNameFromQualifiedName(factoryClassName, false);
         String methodName = null;
         if (methodNames.contains(methodnameShort)) {
             methodName = methodnameLong;
         } else {
             methodName = methodnameShort;
         }
         methodNames.add(methodName);
         return methodName;
    }

    private String getUniqueNameFromQualifiedName(final String factoryClassName, final boolean isShortName) {
        final String[] items = factoryClassName.split("\\.", -1);
        if (items.length >= 2) {
            if (isShortName) {
                final String uniqueName = items[items.length - 1] + getLastDigits(items[items.length - 2]);
                return CodeGen.getCamelCase(uniqueName);
            } else {
                final String uniqueName = items[items.length - 1] + CodeGen.getPascalizeCase(items[items.length - 2]);
                return CodeGen.getCamelCase(uniqueName);
            }
        } else {
            return CodeGen.getCamelCase(factoryClassName);
        }
    }

    private String getLastDigits(final String str) {
        System.out.println("Extract from: " + str);
        final Matcher m = p.matcher(str);
        String lastDigits = "";
        while (m.find()) {
            lastDigits = m.group();
        }
        return lastDigits;
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

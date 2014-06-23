package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.shrinkwrap.descriptor.metadata.Metadata;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataDescriptor;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataElement;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataItem;
import org.jboss.shrinkwrap.descriptor.metadata.MetadataParserPath;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

public enum CommonClassesUtil {
    INSTANCE;

    public synchronized  Map<String, CommonClassItem> getCommonClasses(final Metadata metadata, final MetadataParserPath path) throws IOException {
        final Map<String, CommonClassItem> existingCommonClassMap = new HashMap<String, CommonClassItem>();
        final Set<String> commonPathSet = new HashSet<String>();
        final List<File> fileList = new ArrayList<File>();
        for (final MetadataDescriptor descr : metadata.getMetadataDescriptorList()) {
            if (descr.getCommon() != null) {
                final String pathTo = descr.getCommon().getPathToCommonApi();
                final String commonApi = descr.getCommon().getCommonApi().replace('.', '/');
                commonPathSet.add(pathTo + "/" + commonApi);
            } else if (descr.isGenerateCommonClasses()) {
                final String pathTo = path.getPathToApi();
                final String commonApi = descr.getPackageApi().replaceAll("[0-9]*$", "").replace('.', '/');
                commonPathSet.add(pathTo + "/" + commonApi);
            }
        }

        for (final String pathCommonApi : commonPathSet) {
            listFiles(fileList, pathCommonApi);
        }

        final JavaProjectBuilder builder = new JavaProjectBuilder();
        for (final File file : fileList) {
            if (file.getName().endsWith(".java")) {
                final List<String> extendsList = new ArrayList<String>();
                final JavaSource src = builder.addSource(file);
                final JavaClass class1  = src.getClasses().get(0);
                final List<JavaAnnotation> annotationList = class1.getAnnotations();
                existingCommonClassMap.put(file.getName(), new CommonClassItem(file.getName(), extendsList, class1.getPackageName()));
                for (JavaAnnotation annotation : annotationList) {
                    final  AnnotationValue value = annotation.getProperty("common");
                    final List<String> commonExtendsList = (List<String>)value.getParameterValue();
                    for (String commonClass : commonExtendsList) {
                        extendsList.add(commonClass.replace('"', ' ').trim());
                    }
                }
            }
        }
        return existingCommonClassMap;
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

    public synchronized List<String> findCommonClasses(final Metadata metadata) {
        final List<String> classList = new ArrayList<String>();
        for (MetadataDescriptor descr : metadata.getMetadataDescriptorList()) {
            if (descr.getCommon() != null) {
                traverseClasses(metadata, classList, descr.getRootElementType());
            } else {
                if (descr.isGenerateClasses()) {
                    final String packageApi = descr.getPackageApi();
                    for (MetadataItem item : metadata.getClassList()) {
                        if (packageApi.equals(item.getPackageApi())) {
                            final String type = item.getNamespace() + ":" + item.getName();
                            if (!classList.contains(type)) {
                                classList.add(type);
                            }
                        }
                    }
                }
            }
        }
        return classList;
    }

    private void traverseClasses(final Metadata metadata, final List<String> classList, final String elementType) {
        final String[] elItems = elementType.split(":", -1);
        if (elItems.length == 2) {
            for (MetadataItem item : metadata.getClassList()) {
                if (item.getNamespace().equals(elItems[0]) && item.getName().equals(elItems[1])) {
                    for (MetadataElement element : item.getElements()) {
                        if (!BuilderUtil.isDataType(metadata, element)) {
                            if (!classList.contains(element.getType())) {
                                classList.add(element.getType());
                                traverseClasses(metadata, classList, element.getType());
                            }
                        }
                    }
                    for (MetadataElement element : item.getReferences()) {
                        if (!BuilderUtil.isDataType(metadata, element)) {
                            if (!classList.contains(element.getRef())) {
                                classList.add(element.getRef());
                                traverseClasses(metadata, classList, element.getRef());
                            }
                        }
                    }
                }
            }
            for (MetadataItem item : metadata.getGroupList()) {
                if (item.getNamespace().equals(elItems[0]) && item.getName().equals(elItems[1])) {
                    for (MetadataElement element : item.getElements()) {
                        if (!BuilderUtil.isDataType(metadata, element)) {
                            if (!classList.contains(element.getType())) {
                                classList.add(element.getType());
                                traverseClasses(metadata, classList, element.getType());
                            }
                        }
                    }
                    for (MetadataElement element : item.getReferences()) {
                        if (!BuilderUtil.isDataType(metadata, element)) {
                            if (!classList.contains(element.getRef())) {
                                classList.add(element.getRef());
                                traverseClasses(metadata, classList, element.getRef());
                            }
                        }
                    }
                }
            }
        }
    }

}

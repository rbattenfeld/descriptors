package org.jboss.shrinkwrap.descriptor.metadata.codegen;

import java.util.List;

public class CommonClassItem {

    final String className;
    final List<String> extendsList;
    final String packageAPi;

    public CommonClassItem(final String className, final List<String> extendsList, final String packageApi) {
        this.className = className;
        this.extendsList = extendsList;
        this.packageAPi = packageApi;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getExtendsList() {
        return extendsList;
    }

    public String getPackageAPi() {
        return packageAPi;
    }

}

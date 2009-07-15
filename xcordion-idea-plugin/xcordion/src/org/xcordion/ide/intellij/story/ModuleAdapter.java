package org.xcordion.ide.intellij.story;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;


public class ModuleAdapter {
    private final Module module;

    public ModuleAdapter(Module module) {
        this.module = module;
    }

    public Class load(String className) throws ClassNotFoundException {
        if (className == null) {
            throw new NullPointerException("className should not be null!");
        }
        return new URLClassLoader(classpaths()).loadClass(className);
    }

    public void setAttributes(AntJavaTaskRunner task) {
        task.appendClasspaths(classpaths());
        if (isNotEmpty(encoding())) {
            task.setFileEncoding(encoding());
        }
        if (baseDir() != null) {
            task.setBaseDir(baseDir());
        }
        if (isNotEmpty(jvm())) {
            task.setJvm(jvm());
        }
        if (isNotEmpty(jvmVersion())) {
            task.setJvmVersion(jvmVersion());
        }
//        if (isNotEmpty(maxMemory)) {
//            task.setMaxMemory(maxMemory);
//        }
//        if (isNotEmpty(jvmArgs)) {
//            task.addJvmArgs(jvmArgs);
//        }
    }

    private URL[] classpaths() {
        VirtualFile[] files = manager().getFiles(OrderRootType.CLASSES_AND_OUTPUT);
        Set<URL> fileURLs = new HashSet<URL>();
        for (int i = 0; i < files.length; i++) {
            fileURLs.add(toURL(files[i].getPresentableUrl()));
        }
        VirtualFile test = CompilerModuleExtension.getInstance(module).getCompilerOutputPathForTests();
        if (test != null) {
            fileURLs.add(toURL(test.getPresentableUrl()));
        }
        return fileURLs.toArray(new URL[fileURLs.size()]);
    }

    private URL toURL(String presentableUrl) {
        try {
            return new File(presentableUrl).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Format file \"" + presentableUrl + "\" to URL failed.");
        }
    }

    private String jvmVersion() {
        return getJavaVersion(getJdk().getVersionString());
    }

    private static String getJavaVersion(String javaVersion) {
        if (!javaVersion.matches("[^\\d]*(\\d+\\.\\d+)[^\\d]*.*")) {
            throw new IllegalArgumentException("invalid java version str: " + javaVersion);
        }
        String[] num = javaVersion.split("[^0-9]+");
        if ("".equals(num[0])) {
            return num[1] + "." + num[2];
        }
        return num[0] + "." + num[1];
    }

    private String jvm() {
        String jvm = JavaSdk.getInstance().getVMExecutablePath(getJdk());
        if(new File(jvm).exists()) {
            return jvm;
        }

        for(Sdk sdk : ProjectJdkTable.getInstance().getAllJdks()) {
            jvm = JavaSdk.getInstance().getVMExecutablePath(sdk);
            if(new File(jvm).exists()) {
                return jvm;
            }
        }
        throw new IllegalStateException("Couldn't find out a java sdk vm executable path");
    }

    private File baseDir() {
        return new File(getModuleDir());
    }

    private String encoding() {
        return EncodingManager.getInstance().getDefaultCharsetName();
    }

    private String getModuleDir() {
        VirtualFile parent = module.getModuleFile().getParent();
        if (parent == null) {
            return ".";
        }
        return parent.getPresentableUrl();
    }

    private Sdk getJdk() {
        Sdk jdk = manager().getSdk();
        if (jdk == null) {
            jdk = ProjectRootManager.getInstance(module.getProject()).getProjectJdk();
        }
        if (jdk == null) {
            throw new IllegalArgumentException("the jdk of project and module does not set");
        }
        return jdk;
    }

    private ModuleRootManager manager() {
        return ModuleRootManager.getInstance(module);
    }
}


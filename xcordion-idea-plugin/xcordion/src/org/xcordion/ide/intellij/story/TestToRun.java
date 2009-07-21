package org.xcordion.ide.intellij.story;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

public class TestToRun {
    private String htmlName;
    private Module module;
    private VirtualFile javaFile;
    private String fullyQualifiedClassName;
    private VirtualFile htmlVirtualFile;

    public TestToRun(String testHtmlName) {
        this.htmlName =  testHtmlName;
    }

    public String getHtmlName() {
        return htmlName;
    }

    public Module getModule() {
        return module;
    }

    public void setJavaVirtualFile(VirtualFile testJavaFile) {
        this.javaFile = testJavaFile;
    }

    public VirtualFile getJavaVirtualFile() {
        return javaFile;
    }

    public boolean hasJavaFile() {
        return javaFile != null;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public VirtualFile getHtmlVirtualFile() {
        return htmlVirtualFile;
    }

    public void setHtmlVirtualFile(VirtualFile htmlVirtualFile) {
        this.htmlVirtualFile = htmlVirtualFile;
    }
}

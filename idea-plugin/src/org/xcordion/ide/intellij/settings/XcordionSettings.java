package org.xcordion.ide.intellij.settings;

public class XcordionSettings {

    private String xcordionBackingClassName;

    public String getXcordionBackingClassName() {
        return xcordionBackingClassName;
    }

    public void setXcordionBackingClassName(String xcordionBackingClassName) {
        this.xcordionBackingClassName = xcordionBackingClassName;
    }

    @Override
    public String toString() {
        return "XcordionSettings{" +
                "xcordionBackingClassName='" + xcordionBackingClassName + '\'' +
                '}';
    }
}

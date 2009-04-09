package org.xcordion.ide.intellij.settings;

public class XcordionSettings {

    private String xcordionBackingClassName;
    private boolean showConfirmationMessage;

    public String getXcordionBackingClassName() {
        return xcordionBackingClassName == null ? "" : xcordionBackingClassName;
    }

    public void setXcordionBackingClassName(String xcordionBackingClassName) {
        this.xcordionBackingClassName = xcordionBackingClassName;
    }

    public boolean showConfirmationMessage() {
        return showConfirmationMessage;
    }

    public void setShowConfirmationMessage(boolean showConfirmationMessage) {
        this.showConfirmationMessage = showConfirmationMessage;
    }

    @Override
    public String toString() {
        return "XcordionSettings{" +
                "xcordionBackingClassName='" + xcordionBackingClassName + '\'' +
                ", showConfirmationMessage=" + showConfirmationMessage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XcordionSettings that = (XcordionSettings) o;

        if (showConfirmationMessage != that.showConfirmationMessage) return false;
        if (xcordionBackingClassName != null ? !xcordionBackingClassName.equals(that.xcordionBackingClassName) : that.xcordionBackingClassName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = xcordionBackingClassName != null ? xcordionBackingClassName.hashCode() : 0;
        result = 31 * result + (showConfirmationMessage ? 1 : 0);
        return result;
    }
}

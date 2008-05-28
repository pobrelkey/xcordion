package org.xcordion.ide.intellij;

public enum XcordionAttribute {
    ASSERT_EQUALS ("assertEquals"),
    ASSERT_FALSE ("assertFalse"),
    ASSERT_TRUE ("assertTrue"),
    EXECUTE("execute"),
    FOR_EACH("forEach"),
    INSERT_TEXT("insertText"),
    SET("set");

    private String stringValue;

    XcordionAttribute(String value) {
        this.stringValue = value;
    }

    public String toString() {
        return stringValue;
    }

}

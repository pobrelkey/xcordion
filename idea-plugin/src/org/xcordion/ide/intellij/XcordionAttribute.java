package org.xcordion.ide.intellij;

import com.intellij.psi.xml.XmlAttribute;
import static org.xcordion.ide.intellij.XcordionNamespace.*;

public enum XcordionAttribute {

    EXECUTE(NAMESPACE_CONCORDION_2007, "execute", false),
    SET(NAMESPACE_CONCORDION_2007, "set", true),
    ASSERT_EQUALS(NAMESPACE_CONCORDION_2007, "assertEquals", false),
    VERIFY_ROWS(NAMESPACE_CONCORDION_2007, "verifyRows", false),
    ASSERT_FALSE(NAMESPACE_CONCORDION_2007, "assertFalse", false),
    ASSERT_TRUE(NAMESPACE_CONCORDION_2007, "assertTrue", false),

    OLD_EXECUTE(NAMESPACE_CONCORDION_OLD, "execute", false),
    OLD_SET(NAMESPACE_CONCORDION_OLD, "set", true),
    OLD_ASSERT_EQUALS(NAMESPACE_CONCORDION_OLD, "assertEquals", false),
    OLD_ASSERT_FALSE(NAMESPACE_CONCORDION_OLD, "assertFalse", false),
    OLD_ASSERT_TRUE(NAMESPACE_CONCORDION_OLD, "assertTrue", false),
    OLD_FOREACH(NAMESPACE_CONCORDION_OLD, "forEach", false),
    OLD_INSERT_TEXT(NAMESPACE_CONCORDION_OLD, "insertText", false),

    ANCIENT_EXECUTE(NAMESPACE_CONCORDION_ANCIENT, "execute", false),
    ANCIENT_PARAM(NAMESPACE_CONCORDION_ANCIENT, "param", true),
    ANCIENT_VERIFY(NAMESPACE_CONCORDION_ANCIENT, "verify", false);


    private String namespaceUrl;
    private String localName;
    private boolean setAttribute;

    XcordionAttribute(XcordionNamespace namespaceUrl, String localName, boolean setAttribute) {
        this.namespaceUrl = namespaceUrl.getNamespace();
        this.localName = localName;
        this.setAttribute = setAttribute;
    }

    public String getNamespaceUrl() {
        return namespaceUrl;
    }

    public String getLocalName() {
        return localName;
    }

    public boolean isSetAttribute() {
        return setAttribute;
    }

    static public XcordionAttribute forXmlAttribute(XmlAttribute xmlAttribute) {
        for (XcordionAttribute attribute : XcordionAttribute.values()) {
            if (attribute.namespaceUrl.equals(xmlAttribute.getNamespace()) &&
                    attribute.localName.equals(xmlAttribute.getLocalName())) {
                return attribute;
            }
        }
        return null;
    }

    static public boolean isXcordionAttribute(XmlAttribute attribute) {
        return forXmlAttribute(attribute) != null;
    }

    static public boolean isXcordionSetAttribute(XmlAttribute attribute) {
        XcordionAttribute xcordionAttribute = forXmlAttribute(attribute);
        return xcordionAttribute != null && xcordionAttribute.isSetAttribute();
    }
}

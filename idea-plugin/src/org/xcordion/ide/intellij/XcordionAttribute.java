package org.xcordion.ide.intellij;

import com.intellij.psi.xml.XmlAttribute;
import static org.xcordion.ide.intellij.XcordionNamespace.*;

public enum XcordionAttribute {

    EXECUTE(NAMESPACE_CONCORDION_2007, "execute", XcordionAttributeSyntax.EXECUTE),
    SET(NAMESPACE_CONCORDION_2007, "set", XcordionAttributeSyntax.SET),
    ASSERT_EQUALS(NAMESPACE_CONCORDION_2007, "assertEquals", XcordionAttributeSyntax.EXECUTE),
    VERIFY_ROWS(NAMESPACE_CONCORDION_2007, "verifyRows", XcordionAttributeSyntax.FOREACH),
    ASSERT_FALSE(NAMESPACE_CONCORDION_2007, "assertFalse", XcordionAttributeSyntax.EXECUTE),
    ASSERT_TRUE(NAMESPACE_CONCORDION_2007, "assertTrue", XcordionAttributeSyntax.EXECUTE),

    OLD_EXECUTE(NAMESPACE_CONCORDION_OLD, "execute", XcordionAttributeSyntax.EXECUTE),
    OLD_SET(NAMESPACE_CONCORDION_OLD, "set", XcordionAttributeSyntax.SET),
    OLD_ASSERT_EQUALS(NAMESPACE_CONCORDION_OLD, "assertEquals", XcordionAttributeSyntax.EXECUTE),
    OLD_ASSERT_FALSE(NAMESPACE_CONCORDION_OLD, "assertFalse", XcordionAttributeSyntax.EXECUTE),
    OLD_ASSERT_TRUE(NAMESPACE_CONCORDION_OLD, "assertTrue", XcordionAttributeSyntax.EXECUTE),
    OLD_FOREACH(NAMESPACE_CONCORDION_OLD, "forEach", XcordionAttributeSyntax.FOREACH),
    OLD_INSERT_TEXT(NAMESPACE_CONCORDION_OLD, "insertText", XcordionAttributeSyntax.EXECUTE),

    ANCIENT_EXECUTE(NAMESPACE_CONCORDION_ANCIENT, "execute", XcordionAttributeSyntax.EXECUTE),
    ANCIENT_PARAM(NAMESPACE_CONCORDION_ANCIENT, "param", XcordionAttributeSyntax.SET),
    ANCIENT_VERIFY(NAMESPACE_CONCORDION_ANCIENT, "verify", XcordionAttributeSyntax.EXECUTE);


    private String namespaceUrl;
    private String localName;
    private XcordionAttributeSyntax syntax;

    XcordionAttribute(XcordionNamespace namespaceUrl, String localName, XcordionAttributeSyntax syntax) {
        this.syntax = syntax;
        this.namespaceUrl = namespaceUrl.getNamespace();
        this.localName = localName;
    }

    public String getNamespaceUrl() {
        return namespaceUrl;
    }

    public String getLocalName() {
        return localName;
    }

    public XcordionAttributeSyntax getSyntax() {
        return syntax;
    }

    public boolean isSetAttribute() {
        return syntax == XcordionAttributeSyntax.SET;
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

    public static XcordionAttribute forNamespaceAndName(String namespaceUrl, String localName) {
        for (XcordionAttribute attribute : values()) {
            if (attribute.getNamespaceUrl().equals(namespaceUrl) && attribute.getLocalName().equals(localName)) {
                return attribute;
            }
        }
        return null;
    }
}

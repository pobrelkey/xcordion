package org.xcordion.ide.intellij;

import com.intellij.psi.xml.XmlAttribute;
import static jedi.functional.Coercions.list;
import jedi.functional.Filter;
import static jedi.functional.FunctionalPrimitives.headOrNullIfEmpty;
import static jedi.functional.FunctionalPrimitives.select;
import static org.xcordion.ide.intellij.XcordionNamespace.*;

public enum XcordionAttribute {

    CONTAINS(NAMESPACE_XCORDION, "contains", XcordionAttributeSyntax.EXECUTE),
    DO(NAMESPACE_XCORDION, "do", XcordionAttributeSyntax.EXECUTE),
    DOES_NOT_CONTAIN(NAMESPACE_XCORDION, "doesNotContain", XcordionAttributeSyntax.EXECUTE),
    FOR(NAMESPACE_XCORDION, "for", XcordionAttributeSyntax.FOREACH),
    IGNORE(NAMESPACE_XCORDION, "ignore", XcordionAttributeSyntax.IGNORE),
    IS_EQUAL(NAMESPACE_XCORDION, "isEqual", XcordionAttributeSyntax.EXECUTE),
    IS_FALSE(NAMESPACE_XCORDION, "isFalse", XcordionAttributeSyntax.EXECUTE),
    IS_TRUE(NAMESPACE_XCORDION, "isTrue", XcordionAttributeSyntax.EXECUTE),
    SET(NAMESPACE_XCORDION, "set", XcordionAttributeSyntax.SET),
    SHOW(NAMESPACE_XCORDION, "show", XcordionAttributeSyntax.EXECUTE),

    ASSERT_CONTAINS(NAMESPACE_CONCORDION_2007, "assertContains", XcordionAttributeSyntax.EXECUTE),
    ASSERT_EQUALS(NAMESPACE_CONCORDION_2007, "assertEquals", XcordionAttributeSyntax.EXECUTE),
    ASSERT_FALSE(NAMESPACE_CONCORDION_2007, "assertFalse", XcordionAttributeSyntax.EXECUTE),
    ASSERT_TRUE(NAMESPACE_CONCORDION_2007, "assertTrue", XcordionAttributeSyntax.EXECUTE),
    EXECUTE(NAMESPACE_CONCORDION_2007, "execute", XcordionAttributeSyntax.EXECUTE),
    SET_2007(NAMESPACE_CONCORDION_2007, "set", XcordionAttributeSyntax.SET),
    VERIFY_ROWS(NAMESPACE_CONCORDION_2007, "verifyRows", XcordionAttributeSyntax.FOREACH),

    OLD_ASSERT_CONTAINS(NAMESPACE_CONCORDION_OLD, "assertContains", XcordionAttributeSyntax.EXECUTE),
    OLD_ASSERT_EQUALS(NAMESPACE_CONCORDION_OLD, "assertEquals", XcordionAttributeSyntax.EXECUTE),
    OLD_ASSERT_FALSE(NAMESPACE_CONCORDION_OLD, "assertFalse", XcordionAttributeSyntax.EXECUTE),
    OLD_ASSERT_TRUE(NAMESPACE_CONCORDION_OLD, "assertTrue", XcordionAttributeSyntax.EXECUTE),
    OLD_EXECUTE(NAMESPACE_CONCORDION_OLD, "execute", XcordionAttributeSyntax.EXECUTE),
    OLD_FOREACH(NAMESPACE_CONCORDION_OLD, "forEach", XcordionAttributeSyntax.FOREACH),
    OLD_INSERT_TEXT(NAMESPACE_CONCORDION_OLD, "insertText", XcordionAttributeSyntax.EXECUTE),
    OLD_SET(NAMESPACE_CONCORDION_OLD, "set", XcordionAttributeSyntax.SET),

    ANCIENT_EXECUTE(NAMESPACE_CONCORDION_ANCIENT, "execute", XcordionAttributeSyntax.EXECUTE),
    ANCIENT_PARAM(NAMESPACE_CONCORDION_ANCIENT, "param", XcordionAttributeSyntax.SET),
    ANCIENT_VERIFY(NAMESPACE_CONCORDION_ANCIENT, "verify", XcordionAttributeSyntax.EXECUTE);


    private final String namespaceUrl;
    private final String localName;
    private final XcordionAttributeSyntax syntax;

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

    private static XcordionAttribute forXmlAttribute(XmlAttribute xmlAttribute) {
        return headOrNullIfEmpty(select(list(values()), forNamespaceAndNameFilter(xmlAttribute.getNamespace(), xmlAttribute.getLocalName())));
    }

    public static boolean isXcordionAttribute(XmlAttribute attribute) {
        return forXmlAttribute(attribute) != null;
    }

    public static XcordionAttribute forNamespaceAndName(String namespaceUrl, String localName) {
        return headOrNullIfEmpty(select(list(values()), forNamespaceAndNameFilter(namespaceUrl, localName)));
    }

    private static Filter<XcordionAttribute> forNamespaceAndNameFilter(final String namespaceUrl, final String localName) {
        return new Filter<XcordionAttribute>() {
            public Boolean execute(XcordionAttribute xcordionAttribute) {
                return xcordionAttribute.getNamespaceUrl().equals(namespaceUrl) && xcordionAttribute.getLocalName().equals(localName);
            }
        };
    }
}
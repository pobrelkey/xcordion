package org.xcordion.ide.intellij;

import static jedi.functional.Coercions.list;
import jedi.functional.Filter;
import static jedi.functional.FunctionalPrimitives.select;

import java.util.ArrayList;
import static java.util.Collections.unmodifiableList;
import java.util.List;


public enum XcordionNamespace {
    NAMESPACE_XCORDION("urn:xcordion:v1"),
    NAMESPACE_CONCORDION_2007("http://www.concordion.org/2007/concordion"),
    NAMESPACE_CONCORDION_OLD("http://concordion.org"),
    NAMESPACE_CONCORDION_ANCIENT("http://concordion.org/namespace/concordion-1.0");

    private final String namespace;
    private List<XcordionAttribute> attributes;

    private XcordionNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<XcordionAttribute> getAttributes() {
        if (this.attributes == null) {
            ArrayList<XcordionAttribute> attributes = new ArrayList<XcordionAttribute>();
            attributes.addAll(attributesForCurrentNamespace());
            this.attributes = unmodifiableList(attributes);
        }
        return this.attributes;
    }

    private List<XcordionAttribute> attributesForCurrentNamespace() {
        return select(list(XcordionAttribute.values()), new Filter<XcordionAttribute>() {
            public Boolean execute(XcordionAttribute attribute) {
                return attribute.getNamespaceUrl().equals(namespace);
            }
        });
    }
}

package org.xcordion.ide.intellij;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public enum XcordionNamespace {
    NAMESPACE_CONCORDION_2007("http://www.concordion.org/2007/concordion"),
    NAMESPACE_CONCORDION_OLD("http://concordion.org"),
    NAMESPACE_CONCORDION_ANCIENT("http://concordion.org/namespace/concordion-1.0");

    private String namespace;
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
            for (XcordionAttribute attribute : XcordionAttribute.values()) {
                if (attribute.getNamespaceUrl().equals(namespace)) {
                    attributes.add(attribute);
                }
            }
            this.attributes = Collections.unmodifiableList(attributes);
        }
        return this.attributes;
    }
}

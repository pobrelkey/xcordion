package org.concordion.internal.util;

import org.concordion.api.Element;

public class XmlFunctional {
    public static String getElementOrChildsAttributeValue(Element element, String attributeName) {
        String value = element.getAttributeValue(attributeName);
        if(value == null) {
            Element[] childElements = element.getChildElements();
            for (Element childElement : childElements) {
                value = getElementOrChildsAttributeValue(childElement, attributeName);
                if(value != null) {
                    return value;
                }
            }
        }
        return value;
    }
}

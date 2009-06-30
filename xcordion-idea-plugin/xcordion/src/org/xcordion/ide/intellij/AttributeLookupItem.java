package org.xcordion.ide.intellij;

import com.intellij.codeInsight.lookup.LookupItem;

class AttributeLookupItem extends LookupItem<String> {
    public AttributeLookupItem(String attributeName, String attributeName1) {
        super(attributeName, attributeName1);
    }

    @Override
    public XmlAttributeInsertHandler<AttributeLookupItem> getInsertHandler() {
        return new XmlAttributeInsertHandler<AttributeLookupItem>();
    }
}

package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

class XcordionAttributeCompletionVariant extends CompletionVariant {
    public XcordionAttributeCompletionVariant() {
        super(new XcordionXmlAttributeFilter());
        this.includeScopeClass(LeafPsiElement.class, true);
        this.addCompletion(new XcordionAttributeKeywordChooser());
        this.setInsertHandler(new XmlAttributeInsertHandler());
    }
}

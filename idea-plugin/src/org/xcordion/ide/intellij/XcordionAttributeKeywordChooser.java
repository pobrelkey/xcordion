package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.KeywordChooser;
import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.html.HtmlTag;

import java.util.List;
import java.util.ArrayList;


class XcordionAttributeKeywordChooser implements KeywordChooser {

    public String[] getKeywords(CompletionContext completionContext, PsiElement psiElement) {
        if (isNonNullXmlAttribute(psiElement)) {
            List<String> qualifiedAttributes = new ArrayList<String>();
            for (XcordionNamespace namespace : XcordionNamespace.values()) {
                String namespacePrefix = ((XmlTag) psiElement.getParent().getParent()).getPrefixByNamespace(namespace.getNamespace());
                if (namespacePrefix == null) {
                    continue;
                }
                for (XcordionAttribute attribute : namespace.getAttributes()) {
                    qualifiedAttributes.add(namespacePrefix + ":" + attribute.getLocalName());
                }
            }
            return qualifiedAttributes.toArray(new String[0]);
        }
        return new String[0];
    }

    private static boolean isNonNullXmlAttribute(PsiElement psiElement) {
        return psiElement != null
                && psiElement.getParent() != null
                && psiElement.getParent().getParent() != null
                && (psiElement.getParent().getParent() instanceof XmlTag);
    }

}

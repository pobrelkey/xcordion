package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.KeywordChooser;
import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.html.HtmlTag;

import java.util.List;
import java.util.ArrayList;


class XcordionAttributeKeywordChooser implements KeywordChooser {

    public static final String XCORDION_NAMESPACE = "http://concordion.org";
    public static final String DEFAULT_XCORDION_NAMESPACE_PREFIX = "concordion";


    public String[] getKeywords(CompletionContext completionContext, PsiElement psiElement) {
        if (isNonNullHtmlAttribute(psiElement)) {
            String namespacePrefix = ((HtmlTag) psiElement.getParent().getParent()).getPrefixByNamespace(XCORDION_NAMESPACE);
            namespacePrefix = namespacePrefix==null? DEFAULT_XCORDION_NAMESPACE_PREFIX : namespacePrefix;
            List<String> qualifiedAttributes = new ArrayList<String>();
            for(XcordionAttribute attribute: XcordionAttribute.values()){
                qualifiedAttributes.add(namespacePrefix + ":" + attribute);
            }
            return qualifiedAttributes.toArray(new String[0]);
        }
        return new String[0];
    }

    private static boolean isNonNullHtmlAttribute(PsiElement psiElement) {
        return psiElement != null
                && psiElement.getParent() != null
                && psiElement.getParent().getParent() != null
                && (psiElement.getParent().getParent() instanceof HtmlTag);
    }

}

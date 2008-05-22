package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.codeInsight.completion.KeywordChooser;
import com.intellij.psi.PsiElement;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

public class XcordionAttributeCompletionVariant extends CompletionVariant {
    //TODO need to handle namespaces instead of hardcoding to concordion:
    public static final String[] XCORDION_ATTRIBUTES = new String[]{
            "concordion:execute",
            "concordion:assertEquals",
            "concordion:set"
    };

    public XcordionAttributeCompletionVariant() {
        super(new XcordionXmlAttributeFilter());
        this.includeScopeClass(LeafPsiElement.class, true);
        this.addCompletion(new KeywordChooser() {
            public String[] getKeywords(CompletionContext completionContext, PsiElement psiElement) {
                return isNonNullHtmlAttribute(psiElement) ? XCORDION_ATTRIBUTES : new String[0];
            }
        });
        this.setInsertHandler(new XmlAttributeInsertHandler());
    }

    private static boolean isNonNullHtmlAttribute(PsiElement psiElement) {
        return psiElement != null
                && psiElement.getParent() != null
                && psiElement.getParent().getParent() != null
                && (psiElement.getParent().getParent() instanceof HtmlTag);
    }
}

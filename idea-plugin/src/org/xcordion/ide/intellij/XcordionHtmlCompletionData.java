package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.codeInsight.completion.HtmlCompletionData;
import com.intellij.psi.PsiElement;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: timt
 * Date: 16-May-2008
 * Time: 10:16:03
 * To change this template use File | Settings | File Templates.
 */
public class XcordionHtmlCompletionData extends HtmlCompletionData {

    private CompletionVariant attributeCompletionVariant;
    private CompletionVariant attributeValueCompeletionVariant;

    public XcordionHtmlCompletionData() {
        this.attributeCompletionVariant = new XcordionAttributeCompletionVariant();
        this.attributeValueCompeletionVariant = new XcordionAttributeValueCompetionVariant();
    }

    public void addKeywordVariants(Set<CompletionVariant> completionVariants, CompletionContext completionContext, PsiElement psiElement) {
        super.addKeywordVariants(completionVariants, completionContext, psiElement);    //To change body of overridden methods use File | Settings | File Templates.
        addCustomCompletionVariant(attributeCompletionVariant, completionVariants, psiElement);
        addCustomCompletionVariant(attributeValueCompeletionVariant, completionVariants, psiElement);
    }

    private void addCustomCompletionVariant(CompletionVariant completionVariant, Set<CompletionVariant> completionVariants, PsiElement psiElement) {
        if (completionVariant.isVariantApplicable(psiElement, psiElement)) {
            completionVariants.add(completionVariant);
        }
    }

}

package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.codeInsight.completion.HtmlCompletionData;
import com.intellij.psi.PsiElement;

import java.util.Set;

//TODO: Handle ognl style methods
//TODO: Filter attributeValue completions based on namespace prefix
//TODO: Click through to java on method names
//TODO: Completions after dot on variable names, i.e. #foo. should list bar()
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

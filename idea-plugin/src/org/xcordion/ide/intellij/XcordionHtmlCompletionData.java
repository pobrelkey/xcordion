package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.codeInsight.completion.HtmlCompletionData;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Set;

//TODO: Click through to java on method names
//TODO: Completions after dot on variable names, i.e. #foo. should list bar()
//TODO: Handle carriage returns in attributevalue
public class XcordionHtmlCompletionData extends HtmlCompletionData {

    private CompletionVariant attributeCompletionVariant;
    private CompletionVariant attributeValueCompletionVariant;

    public XcordionHtmlCompletionData() {
        super();
        this.attributeCompletionVariant = new XcordionAttributeCompletionVariant();
        this.attributeValueCompletionVariant = new XcordionAttributeValueCompetionVariant();
        registerVariant(attributeCompletionVariant);
        registerVariant(attributeValueCompletionVariant);
    }

    public void addKeywordVariants(Set<CompletionVariant> completionVariants, PsiElement psiElement, PsiFile psiFile) {
        super.addKeywordVariants(completionVariants, psiElement, psiFile);
        addCustomCompletionVariant(attributeCompletionVariant, completionVariants, psiFile);
        addCustomCompletionVariant(attributeValueCompletionVariant, completionVariants, psiFile);
    }

    private void addCustomCompletionVariant(CompletionVariant completionVariant, Set<CompletionVariant> completionVariants, PsiElement psiElement) {
        if (completionVariant.isVariantApplicable(psiElement, psiElement)) {
            completionVariants.add(completionVariant);
        }
    }
}

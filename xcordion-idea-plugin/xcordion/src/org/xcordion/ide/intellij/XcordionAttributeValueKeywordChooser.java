package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.KeywordChooser;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Handle completions for assignments i.e. #foo=bar() - Rob: this is a bug in IntelliJ, we return values but they get swallowed, perhaps solvable by going back to OpenAPI?
class XcordionAttributeValueKeywordChooser implements KeywordChooser {
    private static final String[] EMPTY_KEYWORD_LIST = new String[0];
    private static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz ";
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*)\\b(\\w+)$");

    public String[] getKeywords(CompletionContext completionContext, PsiElement psiElement) {
        if (interestingElement(psiElement)) {
            XmlAttributeValue attributeValueElement = (XmlAttributeValue) psiElement.getParent();
            String suffix = null;
            String baseExpression = getValueLeftOfCursor(attributeValueElement);
            Matcher suffixMatcher = SUFFIX_PATTERN.matcher(baseExpression);
            if (suffixMatcher.matches()) {
                baseExpression = suffixMatcher.group(1);
                suffix = suffixMatcher.group(2);
            }

            List<String> displayValues = XcordionReflectionUtils.getDisplayValues(attributeValueElement, suffix, baseExpression);

            return displayValues.toArray(new String[displayValues.size()]);
        }
        return EMPTY_KEYWORD_LIST;
    }

    private boolean interestingElement(PsiElement psiElement) {
        PsiElement parent = psiElement.getParent();
        if (!(parent instanceof XmlAttributeValue)) {
            return false;
        }
        XmlAttribute attribute = (XmlAttribute) parent.getParent();
        return XcordionAttribute.isXcordionAttribute(attribute);
    }


    private String getValueLeftOfCursor(PsiElement psiElement) {
        return psiElement.getText().substring(1, psiElement.getText().indexOf(INTELLIJ_IDEA_RULEZZZ));
    }
}

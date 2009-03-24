package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupItem;
import static com.intellij.openapi.application.ApplicationManager.getApplication;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XcordionCompletionContributor extends CompletionContributor {
    private static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz ";
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*)\\b(\\w+)$");

    @Override
    public boolean fillCompletionVariants(final CompletionParameters parameters, final CompletionResultSet result) {
        final PsiElement position = parameters.getPosition();
        if (!xmlFile(parameters.getOriginalFile())) {
            return true;
        }

        if (parameters.getCompletionType() == CompletionType.BASIC && position instanceof XmlToken) {
            getApplication().runReadAction(new Runnable() {
                public void run() {
                    completeResults(position, result);
                }
            });
            return false;
        }
        return true;
    }

    private void completeResults(PsiElement position, CompletionResultSet result) {
        XmlToken token = (XmlToken) position;
        final PsiElement parent = token.getParent();

        if (validAttribute(token, parent)) {
            XmlAttribute attribute = (XmlAttribute) parent;
            PsiElement tag = attribute.getParent();

            if (xmlTag(tag)) {
                completeWithAttributeNames(result, parent);
            }
        } else if (validAttributeValue(token, parent)) {
            completeWithTagNames((XmlAttributeValue) parent, result);
        }
    }

    private boolean validAttributeValue(XmlToken token, PsiElement parent) {
        return token.getTokenType().toString().equals("XML_ATTRIBUTE_VALUE_TOKEN") && parent instanceof XmlAttributeValue;
    }

    private boolean validAttribute(XmlToken token, PsiElement parent) {
        return token.getTokenType().toString().equals("XML_NAME") && parent instanceof XmlAttribute;
    }

    private boolean xmlTag(PsiElement tag) {
        return tag instanceof XmlTag;
    }

    private boolean xmlFile(PsiFile currentFile) {
        return currentFile instanceof XmlFile;
    }

    private void completeWithTagNames(XmlAttributeValue attributeValue, CompletionResultSet result) {
        String suffix = null;
        String baseExpression = getValueLeftOfCursor(attributeValue);
        Matcher suffixMatcher = SUFFIX_PATTERN.matcher(baseExpression);
        if (suffixMatcher.matches()) {
            baseExpression = suffixMatcher.group(1);
            suffix = suffixMatcher.group(2);
        }

        List<String> displayValues = XcordionReflectionUtils.getDisplayValues(attributeValue, suffix, baseExpression);

        for (String displayValue : displayValues) {
            result.addElement(new LookupItem<String>(displayValue, displayValue));
        }
    }

    private String getValueLeftOfCursor(PsiElement psiElement) {
        return psiElement.getText().substring(1, psiElement.getText().indexOf(INTELLIJ_IDEA_RULEZZZ));
    }

    private void completeWithAttributeNames(CompletionResultSet result, PsiElement parent) {
        for (XcordionNamespace namespace : XcordionNamespace.values()) {
            String namespacePrefix = ((XmlTag) parent.getParent().getParent()).getPrefixByNamespace(namespace.getNamespace());
            if (namespacePrefix == null) {
                continue;
            }
            for (XcordionAttribute xattribute : namespace.getAttributes()) {
                String attributeName = namespacePrefix + ":" + xattribute.getLocalName();
                result.addElement(new AttributeLookupItem(attributeName, attributeName));
            }
        }
    }

    private static class AttributeLookupItem extends LookupItem<String> {
        public AttributeLookupItem(String attributeName, String attributeName1) {
            super(attributeName, attributeName1);
        }

        @Override
        public XmlAttributeInsertHandler<AttributeLookupItem> getInsertHandler() {
            return new XmlAttributeInsertHandler<AttributeLookupItem>();
        }
    }
}

package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.simple.SimpleLookupItem;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementFactoryImpl;
import com.intellij.codeInsight.lookup.LookupItem;
import static com.intellij.openapi.application.ApplicationManager.getApplication;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.*;
import com.intellij.util.text.CharArrayUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XcordionCompletionContributor extends CompletionContributor {
    private static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz ";
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*)\\b(\\w+)$", Pattern.DOTALL);

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

    // intended to prevent pre-existing text in the destination OGNL that happens to match our copmpletion test from getting deleted
    // in our custom InsertHandler (particualrly at the point where the magickIndex variable is used) 
    private static final String OUR_MAGICKAL_STRING = "lihugrgilugluygerqhbgagrsd";

    private void completeWithTagNames(XmlAttributeValue attributeValue, CompletionResultSet result) {
        String suffix = null;
        String baseExpression = getValueLeftOfCursor(attributeValue);
        Matcher suffixMatcher = SUFFIX_PATTERN.matcher(baseExpression);
        if (suffixMatcher.matches()) {
            baseExpression = suffixMatcher.group(1);
            suffix = suffixMatcher.group(2);
        }

        List<AutoCompleteItem> displayValues = XcordionReflectionUtils.getAutoCompleteItems(attributeValue, suffix, baseExpression);

        for (AutoCompleteItem autoCompleteItem : displayValues) {
            SimpleLookupItem<String> item = LookupElementFactoryImpl.getInstance().createLookupElement(autoCompleteItem.getText());

            String insertableString = getInsertableString(baseExpression, autoCompleteItem.getText());

            String insertableTextThatWillKeepIntelliJHappy = getInsertableTextWithNoCharactersIntelliJCantHandle(insertableString);
            item.setLookupString(insertableTextThatWillKeepIntelliJHappy + OUR_MAGICKAL_STRING);

            item.setTypeText(autoCompleteItem.getType()); // return type
            item.setAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE);
            item.setPresentableText(autoCompleteItem.getText());

            item.setInsertHandler(getInsertHandler(insertableString));

            result.addElement(item);
        }
    }

    private String getInsertableTextWithNoCharactersIntelliJCantHandle(String insertableString) {
        return insertableString.replaceAll("\n", "");
    }

    private String getInsertableString(String baseExpression, String displayValue) {
        String insertableString = displayValue;
        if (baseExpression.trim().endsWith(".") && !autoCompletePerformedOnNewLine(baseExpression)) {
            // for some reason when we auto complete when on the end of a '.' we have to add the baseExpression too (but only when it was performed on the same line). Who knows why :S
            insertableString = baseExpression + insertableString;
        }
        return insertableString;
    }

    private boolean autoCompletePerformedOnNewLine(String baseExpression) {
        int indexOfDot = baseExpression.lastIndexOf(".");
        int indexOfNewLine = baseExpression.lastIndexOf("\n");
        return indexOfNewLine > indexOfDot;
    }

    private BasicInsertHandler<LookupElement> getInsertHandler(final String insertable) {
        return new BasicInsertHandler<LookupElement>() {
            @Override
            public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement) {
                Editor editor = insertionContext.getEditor();
                
                Document document = editor.getDocument();
                int caretModelOffset = editor.getCaretModel().getOffset();

                CharSequence charsequence = document.getCharsSequence();
                int magickIndex = CharArrayUtil.indexOf(charsequence, lookupElement.getLookupString(), 0);
                if (magickIndex != -1) {
                    caretModelOffset = magickIndex;

                    document.deleteString(magickIndex, magickIndex + lookupElement.getLookupString().length());
                }
                document.insertString(caretModelOffset, insertable);
                editor.getCaretModel().moveToOffset(caretModelOffset + insertable.length());
                editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
                editor.getSelectionModel().removeSelection();
            }
        };
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

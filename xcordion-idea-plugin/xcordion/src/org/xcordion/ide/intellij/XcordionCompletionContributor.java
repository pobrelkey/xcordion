package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.simple.SimpleLookupItem;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementFactoryImpl;
import static com.intellij.openapi.application.ApplicationManager.getApplication;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.*;
import com.intellij.util.text.CharArrayUtil;
import static jedi.functional.Coercions.list;
import jedi.functional.Filter;
import static jedi.functional.FunctionalPrimitives.collect;
import static jedi.functional.FunctionalPrimitives.select;
import jedi.functional.Functor;
import static org.xcordion.ide.intellij.XcordionAttribute.IGNORE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XcordionCompletionContributor extends CompletionContributor {
    private static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz ";
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*)\\b(\\w+)$", Pattern.DOTALL);
    private static final Pattern IGNORE_ATTRIBUTE_PATTERN = Pattern.compile("\\w+:" + Pattern.quote(IGNORE.getLocalName()) + "=\"(?:^\"|" + Pattern.quote(INTELLIJ_IDEA_RULEZZZ) + ")+");

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

    private boolean xmlFile(PsiFile file) {
        return file instanceof XmlFile;
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

        List<AutoCompleteItem> autoCompleteItems = getAutoCompleteItems(attributeValue, suffix, baseExpression);

        for (AutoCompleteItem autoCompleteItem : autoCompleteItems) {
            SimpleLookupItem<String> item = LookupElementFactoryImpl.getInstance().createLookupElement(autoCompleteItem.getText());

            String insertableString = getInsertableString(baseExpression, autoCompleteItem.getText());

            String insertableTextThatWillKeepIntelliJHappy = getInsertableTextWithNoDodgyCharacters(insertableString);
            item.setLookupString(insertableTextThatWillKeepIntelliJHappy + OUR_MAGICKAL_STRING);

            item.setTypeText(autoCompleteItem.getType()); // return type
            item.setAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE);
            item.setPresentableText(autoCompleteItem.getText());

            item.setInsertHandler(getInsertHandler(insertableString));

            result.addElement(item);
        }
    }

    private List<AutoCompleteItem> getAutoCompleteItems(XmlAttributeValue attributeValue, String suffix, String baseExpression) {
        if (isIgnoreAttribute(attributeValue)) {
            return collect(IgnoreAttributeValue.values(), new Functor<IgnoreAttributeValue, AutoCompleteItem>() {
                public AutoCompleteItem execute(IgnoreAttributeValue value) {
                    return new AutoCompleteItem(value.name().toLowerCase(), "");
                }
            });
        }
        return XcordionReflectionUtils.getAutoCompleteItems(attributeValue, suffix, baseExpression);
    }

    private boolean isIgnoreAttribute(XmlAttributeValue attributeValue) {
        return IGNORE_ATTRIBUTE_PATTERN.matcher(attributeValue.getParent().getText()).matches();
    }

    private String getInsertableTextWithNoDodgyCharacters(String insertableString) {
        return insertableString.replaceAll("\n", "");
    }

    private List<Character> dodgyFirstCharacters = new ArrayList<Character>() {{
        add('#');
        add(' ');
        add('\n');
    }};

    private String getInsertableString(String baseExpression, String displayValue) {
        String insertableString = displayValue;                                                 // dont ask why
        if ((isAutoCompleteOnDot(baseExpression) || isAutoCompleteInBraces(baseExpression)) && !baseExpression.endsWith("build.")) {
            // for some reason when we auto complete when on the end of a '.' we have to add the baseExpression too (but only when it was performed on the same line). Who knows why :S
            insertableString = baseExpression + insertableString;
        }

        insertableString = removeDodgyFirstCharacter(insertableString);

        return insertableString;
    }

    private boolean isAutoCompleteInBraces(String baseExpression) {
        return baseExpression.endsWith("(");
    }

    private boolean isAutoCompleteOnDot(String baseExpression) {
        int indexOfDot = baseExpression.lastIndexOf(".");
        int indexOfNewLine = baseExpression.lastIndexOf("\n");
        boolean autoCompletePerformedOnNewLine = indexOfNewLine > indexOfDot;

        return baseExpression.trim().endsWith(".") && !autoCompletePerformedOnNewLine;
    }

    private String removeDodgyFirstCharacter(String insertableString) {
        for (Character dodgyFirstCharacter : dodgyFirstCharacters) {
            if (insertableString.startsWith(String.valueOf(dodgyFirstCharacter))) {
                insertableString = insertableString.replaceFirst(String.valueOf(dodgyFirstCharacter), "");
            }
        }
        return insertableString.trim();
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

    private void completeWithAttributeNames(final CompletionResultSet result, PsiElement element) {
        PsiElement grandParent = element.getParent().getParent();

        if (xmlTag(grandParent)) {
            for (XcordionNamespace namespace : getXcordionNamespacesInUse(grandParent)) {
                addAttributesForNamespace(result, namespace, getNamespacePrefix(namespace, grandParent));
            }
        }
    }

    private List<XcordionNamespace> getXcordionNamespacesInUse(final PsiElement grandParent) {
        return select(list(XcordionNamespace.values()), new Filter<XcordionNamespace>() {
            public Boolean execute(XcordionNamespace xcordionNamespace) {
                return getNamespacePrefix(xcordionNamespace, grandParent) != null;
            }
        });
    }

    private String getNamespacePrefix(XcordionNamespace xcordionNamespace, PsiElement grandParent) {
        return ((XmlTag) grandParent).getPrefixByNamespace(xcordionNamespace.getNamespace());
    }

    private void addAttributesForNamespace(CompletionResultSet result, XcordionNamespace namespace, String namespacePrefix) {
        for (XcordionAttribute xcordionAttribute : namespace.getAttributes()) {
            String attributeName = namespacePrefix + ":" + xcordionAttribute.getLocalName();
            result.addElement(new AttributeLookupItem(attributeName, attributeName));
        }
    }

    private enum IgnoreAttributeValue {
        IGNORE,
        NO,
        OMIT
    }
}

package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.HtmlCompletionData;
import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.codeInsight.completion.KeywordChooser;
import com.intellij.codeInsight.completion.BasicInsertHandler;
import com.intellij.codeInsight.completion.LookupData;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.xml.util.HtmlUtil;
import com.intellij.util.text.CharArrayUtil;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: timt
 * Date: 16-May-2008
 * Time: 10:16:03
 * To change this template use File | Settings | File Templates.
 */
public class XcordionHtmlCompletionData extends HtmlCompletionData {
    private static final String[] XCORDION_ATTRIBUTES = new String[]{
            "concordion:execute",
            "concordion:assertEquals",
            "concordion:set"
    };
    private CompletionVariant completionVariant;

    public XcordionHtmlCompletionData() {
        this.completionVariant = createCompletionVariant();
    }

    public void addKeywordVariants(Set<CompletionVariant> completionVariants, CompletionContext completionContext, PsiElement psiElement) {
        super.addKeywordVariants(completionVariants, completionContext, psiElement);    //To change body of overridden methods use File | Settings | File Templates.
        completionVariants.add(completionVariant);
    }

    private CompletionVariant createCompletionVariant() {
        CompletionVariant completionVariant = new CompletionVariant(createAttributeCompletionFilter());
        completionVariant.includeScopeClass(LeafPsiElement.class, true);
        completionVariant.addCompletion(new KeywordChooser() {
            public String[] getKeywords(CompletionContext completionContext, PsiElement psiElement) {
                return isNonNullHtmlAttribute(psiElement)?XcordionHtmlCompletionData.XCORDION_ATTRIBUTES:new String[0];
            }
        });
        completionVariant.setInsertHandler(new XmlAttributeInsertHandler());
        return completionVariant;
    }

    private static boolean isNonNullHtmlAttribute(PsiElement psiElement)
    {
        return psiElement != null && psiElement.getParent() != null && psiElement.getParent().getParent() != null && (psiElement.getParent().getParent() instanceof HtmlTag);
    }
    private static class XmlAttributeInsertHandler extends BasicInsertHandler {

        public void handleInsert(CompletionContext completioncontext, int i, LookupData lookupdata, LookupItem lookupitem, boolean flag, char c) {
            super.handleInsert(completioncontext, i, lookupdata, lookupitem, flag, c);
            Editor editor = completioncontext.editor;
            Document document = editor.getDocument();
            int caretModelOffset = editor.getCaretModel().getOffset();
            if (PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document).getFileType() == StdFileTypes.HTML && HtmlUtil.isSingleHtmlAttribute((String) lookupitem.getObject()))
                return;
            CharSequence charsequence = document.getCharsSequence();
            if (!CharArrayUtil.regionMatches(charsequence, caretModelOffset, "=\"") && !CharArrayUtil.regionMatches(charsequence, caretModelOffset, "='"))
                if (caretModelOffset >= document.getTextLength() || "/> \n\t\r".indexOf(document.getCharsSequence().charAt(caretModelOffset)) < 0)
                    document.insertString(caretModelOffset, "=\"\" ");
                else
                    document.insertString(caretModelOffset, "=\"\"");
            editor.getCaretModel().moveToOffset(caretModelOffset + 2);
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
            editor.getSelectionModel().removeSelection();
        }

    }

}

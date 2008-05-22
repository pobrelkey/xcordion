package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.BasicInsertHandler;
import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.LookupData;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.text.CharArrayUtil;
import com.intellij.xml.util.HtmlUtil;

class XmlAttributeInsertHandler extends BasicInsertHandler {

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

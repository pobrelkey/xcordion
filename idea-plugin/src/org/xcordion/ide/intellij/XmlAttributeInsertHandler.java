package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.BasicInsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.text.CharArrayUtil;
import com.intellij.xml.util.HtmlUtil;

class XmlAttributeInsertHandler extends BasicInsertHandler {

    @Override
    public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement) {
        super.handleInsert(insertionContext, lookupElement);
        Editor editor = insertionContext.getEditor();
        Document document = editor.getDocument();
        int caretModelOffset = editor.getCaretModel().getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
        String qualifiedAttributeName = lookupElement.getObject().toString();
        if (psiFile.getFileType() == StdFileTypes.HTML && HtmlUtil.isSingleHtmlAttribute(qualifiedAttributeName)) {
            return;
        }

        PsiElement psiElement = psiFile.findElementAt(caretModelOffset);
        while (psiElement != null && !(psiElement instanceof XmlTag)) {
            psiElement = psiElement.getParent();
        }
        XmlTag tag = (XmlTag) psiElement;
        int whereColon = qualifiedAttributeName.indexOf(':');
        String namespace = tag.getNamespace();
        String localName = qualifiedAttributeName;
        if (whereColon != -1) {
            namespace = tag.getNamespaceByPrefix(qualifiedAttributeName.substring(0, whereColon));
            localName = qualifiedAttributeName.substring(whereColon + 1);
        }
        XcordionAttribute attribute = XcordionAttribute.forNamespaceAndName(namespace, localName);
        boolean insertHash = (attribute != null && attribute.getSyntax() != XcordionAttributeSyntax.EXECUTE);

        CharSequence charsequence = document.getCharsSequence();
        if (!CharArrayUtil.regionMatches(charsequence, caretModelOffset, "=\"") && !CharArrayUtil.regionMatches(charsequence, caretModelOffset, "='")) {
            if (caretModelOffset >= document.getTextLength() || "/> \n\t\r".indexOf(document.getCharsSequence().charAt(caretModelOffset)) < 0) {
                document.insertString(caretModelOffset, insertHash ? "=\"#\" " : "=\"\" ");
            } else {
                document.insertString(caretModelOffset, insertHash ? "=\"#\"" : "=\"\"");
            }
        }
        editor.getCaretModel().moveToOffset(caretModelOffset + (insertHash ? 3 : 2));
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        editor.getSelectionModel().removeSelection();
    }
}

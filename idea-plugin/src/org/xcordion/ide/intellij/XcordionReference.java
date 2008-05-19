package org.xcordion.ide.intellij;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Editor;
import com.intellij.ide.DataManager;
import com.intellij.codeInsight.lookup.LookupValueFactory;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: timt
 * Date: 16-May-2008
 * Time: 13:55:50
 * To change this template use File | Settings | File Templates.
 */
public class XcordionReference extends PsiReferenceBase {
    private PsiManager psiManager;
    private PsiClass psiClass;
    private XmlAttributeValue attributeValue;

    public XcordionReference(PsiElement element, PsiClass psiClass) {
        super(element);
        this.psiClass = psiClass;
        this.psiManager = PsiManager.getInstance(element.getProject());
        attributeValue = (XmlAttributeValue) getElement();
    }

    @Nullable
    public PsiElement resolve() {
        PsiMethod[] methods = psiClass.findMethodsByName(attributeValueToString(attributeValue), true);
        return methods.length > 0 ? methods[0] : null;
    }

    public final Object[] getVariants() {
        //TODO: Return an array of LookupValue* instead
        //Need to to handle method chainging, ie. builders().blahBuilder().withDefaults().build()
        //Need to handle properties in methods, i.e., asDate(#somedate) or asDate('2008-05-13')
        //Need to handle methods in methods, i.e., asDate(someFunction(#aValue, 'some text'), #bvalue)
        //Need to hand ognl comma separator, i.e., build(), doSomethingElse(#now)
        //Add support ognl style getMethods as properties, i.e. getBlah(), can also be referenced as blah
        return psiClass.getMethods();
    }

    private String attributeValueToString(XmlAttributeValue xmlattributevalue) {
        String attributeValueText = xmlattributevalue.getText();
        int cursorPosition = attributeValueText.indexOf("IntellijIdeaRulezzz ");
        if (cursorPosition == -1)
            cursorPosition = getCursorPositionInElementText(xmlattributevalue) + 1;
        return attributeValueText.substring(1, cursorPosition);
    }

    private int getCursorPositionInElementText(PsiElement psiElement) {
        String elementText = psiElement.getText();
        String elementTextWithFirstLetterStriped = elementText.substring(1, elementText.length() - 1);
        Editor editor = (Editor) DataManager.getInstance().getDataContext().getData("editor");
        int j = editor.getCaretModel().getOffset() - psiElement.getTextOffset();
        if (editor != null && SwingUtilities.isEventDispatchThread() && j >= 0 && j <= elementTextWithFirstLetterStriped.length())
            return j;
        else
            return elementTextWithFirstLetterStriped.length();
    }


}

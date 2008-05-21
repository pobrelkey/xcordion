package org.xcordion.ide.intellij;

import com.intellij.ide.DataManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: timt
 * Date: 16-May-2008
 * Time: 13:55:50
 * To change this template use File | Settings | File Templates.
 */
public class XcordionReference extends PsiReferenceBase<XmlAttributeValue> {
    private PsiClass psiClass;
    private XmlAttributeValue attributeValue;
    List<String> excludedMethods = new ArrayList<String>(){{
            add("clone");
            add("finalize");
            add("Object");
            add("registerNatives");
        }};

    public XcordionReference(XmlAttributeValue element, PsiClass psiClass) {
        super(element);
        this.psiClass = psiClass;
        attributeValue = (XmlAttributeValue) getElement();
        excludedMethods.add(psiClass.getName());

    }

    @Nullable
    public PsiElement resolve() {
        PsiMethod[] methods = psiClass.findMethodsByName(getAttributeValueAsString(), true);
        return methods.length > 0 ? methods[0] : null;
    }

    //TODO: Return an array of LookupValue* instead
    public final Object[] getVariants() {
        List<String> displayValues = new ArrayList<String>();
        String attributeValue = getAttributeValueAsString();
        for(PsiMethod method:psiClass.getAllMethods()){
            if(!excludedMethods.contains(method.getName()) && !displayValues.contains(method.getName())){
                displayValues.add(method.getName());
            }
        }
        return displayValues.toArray();
    }

    private String getAttributeValueAsString() {
        String attributeValueText = getElement().getText();
        int cursorPosition = attributeValueText.indexOf("IntellijIdeaRulezzz ");
        if (cursorPosition == -1)
            cursorPosition = getCursorPositionInElementText() + 1;
        return attributeValueText.substring(1, cursorPosition);
    }

    private int getCursorPositionInElementText() {
        String elementText = getElement().getText();
        String elementTextWithFirstLetterStriped = elementText.substring(1, elementText.length() - 1);
        Editor editor = (Editor) DataManager.getInstance().getDataContext().getData("editor");
        int j = editor.getCaretModel().getOffset() - getElement().getTextOffset();
        if (editor != null && SwingUtilities.isEventDispatchThread() && j >= 0 && j <= elementTextWithFirstLetterStriped.length())
            return j;
        else
            return elementTextWithFirstLetterStriped.length();
    }


}

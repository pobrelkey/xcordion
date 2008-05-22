package org.xcordion.ide.intellij;

import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;

class XcordionXmlAttributeValueFilter implements ElementFilter {

    public boolean isAcceptable(Object element, PsiElement context) {
        return (((PsiElement)element).getParent() instanceof XmlAttributeValue);
    }

    public boolean isClassAcceptable(Class hintClass) {
        return true;
    }
}

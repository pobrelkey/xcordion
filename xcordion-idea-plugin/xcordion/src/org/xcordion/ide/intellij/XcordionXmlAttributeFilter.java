package org.xcordion.ide.intellij;

import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.xml.XmlAttribute;

class XcordionXmlAttributeFilter implements ElementFilter {
    public boolean isAcceptable(Object element, PsiElement context) {
        return (((PsiElement) element).getParent() instanceof XmlAttribute);
    }

    public boolean isClassAcceptable(Class hintClass) {
        return true;
    }
}

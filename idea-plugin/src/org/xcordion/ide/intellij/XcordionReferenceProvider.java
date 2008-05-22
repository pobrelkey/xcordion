package org.xcordion.ide.intellij;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

@Deprecated
//This is now being done using a CompletionVariant
public class XcordionReferenceProvider implements PsiReferenceProvider {

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttribute attribute = ((XmlAttribute) psiElement.getParent());
        //TODO: Look properly at name spaces
        //TODO: Move this check into a filter in the XcordionProject when registering
        if (attribute.getName().startsWith("concordion:")) {
            PsiFile file = psiElement.getContainingFile().getOriginalFile();
            if (file == null) {
                file = psiElement.getOriginalElement().getContainingFile();
            }
            //TODO: Align test naming conventions with those used by xcordion
            String qualifiedPackageName = file.getContainingDirectory().getPackage().getQualifiedName();
            String className = file.getName().substring(0, file.getName().length() - 5) + "Test";
            String qualifiedClassName = qualifiedPackageName + "." + className;
            final PsiClass psiClass = PsiManager.getInstance(psiElement.getProject())
                    .findClass(qualifiedClassName, attribute.getResolveScope());
            if (psiClass != null) {
                //TODO: Investigate if can use PsiReferenceExpressionImpl that is part of idea, instead of customer PsiReference implementation for java completions
                PsiReferenceExpressionImpl javaExpressionReference = new PsiReferenceExpressionImpl();
                try {
                    javaExpressionReference.bindToElement(psiClass);
                } catch (IncorrectOperationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                return new PsiReference[]{javaExpressionReference};
                return new PsiReference[]{new XcordionReference((XmlAttributeValue) psiElement, psiClass)};
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement, ReferenceType referenceType) {
        return PsiReference.EMPTY_ARRAY;
    }

    @NotNull
    public PsiReference[] getReferencesByString(String s, PsiElement psiElement, ReferenceType referenceType, int i) {
        return PsiReference.EMPTY_ARRAY;
    }

    public void handleEmptyContext(PsiScopeProcessor psiScopeProcessor, PsiElement psiElement) {
        //NOOP
    }
}

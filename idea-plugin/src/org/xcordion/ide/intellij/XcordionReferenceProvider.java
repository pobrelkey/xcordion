package org.xcordion.ide.intellij;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

@Deprecated
//This is now being done using a CompletionVariant
public class XcordionReferenceProvider extends PsiReferenceProvider {

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement, ProcessingContext processingContext) {
        XmlAttribute attribute = ((XmlAttribute) psiElement.getParent());
        //TODO: Move this check into a filter in the XcordionProject when registering
        if (XcordionAttribute.isXcordionAttribute(attribute)) {
            PsiJavaFile file = (PsiJavaFile) psiElement.getContainingFile().getOriginalFile();
            if (file == null) {
                file = (PsiJavaFile) psiElement.getOriginalElement().getContainingFile();
            }
            //TODO: Align test naming conventions with those used by xcordion

            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(file.getContainingDirectory());
            String qualifiedPackageName = psiPackage.getQualifiedName();
            String className = file.getName().substring(0, file.getName().length() - 5) + "Test";
            String qualifiedClassName = qualifiedPackageName + "." + className;

            final PsiClass psiClass = JavaPsiFacade.getInstance(psiElement.getProject()).findClass(qualifiedClassName, psiElement.getResolveScope());
            if (psiClass != null) {
                //TODO: Investigate if can use PsiReferenceExpressionImpl that is part of idea, instead of customer PsiReference implementation for java completions
                PsiReferenceExpressionImpl javaExpressionReference = new PsiReferenceExpressionImpl();
                try {
                    javaExpressionReference.bindToElement(psiClass);
                } catch (IncorrectOperationException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                return new PsiReference[]{javaExpressionReference};
                return new PsiReference[]{new XcordionReference((XmlAttributeValue) psiElement, psiClass)};
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}

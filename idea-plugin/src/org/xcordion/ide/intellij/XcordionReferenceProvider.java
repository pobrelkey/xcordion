package org.xcordion.ide.intellij;

import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: timt
 * Date: 16-May-2008
 * Time: 13:53:03
 * To change this template use File | Settings | File Templates.
 */
public class XcordionReferenceProvider implements PsiReferenceProvider {

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttribute attribute = ((XmlAttribute) psiElement.getParent());
        if(attribute.getName().startsWith("concordion:")){
            PsiFile file = psiElement.getContainingFile().getOriginalFile();
            if(file==null){
                file = psiElement.getOriginalElement().getContainingFile();
            }
            String qualifiedPackageName = file.getContainingDirectory().getPackage().getQualifiedName();
            String className = file.getName().substring(0, file.getName().length()-5) + "Test";
            String qualifiedClassName = qualifiedPackageName + "." + className;
            final PsiClass psiClass = PsiManager.getInstance(psiElement.getProject())
                    .findClass(qualifiedClassName, attribute.getResolveScope());
            if(psiClass != null){
                //TODO: Investigate if can use PsiReferenceExpressionImpl that is part of idea, instead of customer PsiReference implementation for java completions
//                PsiReferenceExpressionImpl javaExpressionReference = new PsiReferenceExpressionImpl();
//                javaExpressionReference.bindToElement(psiClass);
//                return new PsiReference[]{javaExpressionReference};
                return new PsiReference[]{new XcordionReference(psiElement, psiClass)};
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

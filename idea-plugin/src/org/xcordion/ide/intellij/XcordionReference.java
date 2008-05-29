package org.xcordion.ide.intellij;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
//This is now being done using a CompletionVariant
public class XcordionReference extends PsiReferenceBase<XmlAttributeValue> {
    private PsiClass psiClass;
    private PsiManager psiManager;
    private static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz ";

    public XcordionReference(XmlAttributeValue element, PsiClass psiClass) {
        super(element);
        this.psiClass = psiClass;
        psiManager = PsiManager.getInstance(getModule().getProject());
    }

    @Nullable
    public PsiElement resolve() {
        PsiMethod[] methods = psiClass.findMethodsByName(getValueLeftOfCursor(), true);
        return methods.length > 0 ? methods[0] : null;
    }


    //TODO: Return an array of LookupValue* instead
    public final Object[] getVariants() {
        PsiClass clazz = psiClass;
        String suffix = null;
        String valueLeftOfCursor = getValueLeftOfCursor();
        Matcher suffixMatcher = SUFFIX_PATTERN.matcher(valueLeftOfCursor);
        String baseExpression = valueLeftOfCursor;
        if (suffixMatcher.matches()) {
            baseExpression = suffixMatcher.group(1);
            suffix = suffixMatcher.group(2);
        }

        clazz = XcordionReflectionUtils.findMember(baseExpression, getElement());
        if (clazz == null) {
            return new Object[0];
        }

        List<String> displayValues = XcordionReflectionUtils.getDisplayValues(getElement(), suffix, baseExpression);
        return displayValues.toArray();
    }


    private String getValueLeftOfCursor() {
        return getElement().getText().substring(this.getRangeInElement().getStartOffset(), Math.min(getElement().getText().indexOf(INTELLIJ_IDEA_RULEZZZ), this.getRangeInElement().getEndOffset()));
    }

    static private final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*)\\b(\\w+)$");


    public TextRange getRangeInElement() {
        TextRange rangeInElement = super.getRangeInElement();
        System.out.println("rangeInElement = " + rangeInElement);
        return rangeInElement;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public String getCanonicalText() {
        return super.getCanonicalText();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return super.handleElementRename(newElementName);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return super.bindToElement(element);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isReferenceTo(PsiElement element) {
        return super.isReferenceTo(element);  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean isSoft() {
        return super.isSoft();  //To change body of implemented methods use File | Settings | File Templates.
    }

}

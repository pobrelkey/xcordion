package org.xcordion.ide.intellij;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
//This is now being done using a CompletionVariant
public class XcordionReference extends PsiReferenceBase<XmlAttributeValue> {
    private PsiClass psiClass;
    private PsiManager psiManager;
    List<String> excludedMethods = new ArrayList<String>() {
        {
            add("clone");
            add("finalize");
            add("Object");
            add("registerNatives");
        }
    };
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
        List<String> displayValues = new ArrayList<String>();

        PsiClass clazz = psiClass;
        String suffix = null;
        String valueLeftOfCursor = getValueLeftOfCursor();
        Matcher suffixMatcher = SUFFIX_PATTERN.matcher(valueLeftOfCursor);
        String baseExpression = valueLeftOfCursor;
        if (suffixMatcher.matches()) {
            baseExpression = suffixMatcher.group(1);
            suffix = suffixMatcher.group(2);
        }

        clazz = findMember(baseExpression);
        if (clazz == null) {
            return new Object[0];
        }

        //TODO also need to autocomplete on fields and ognl pseudo fields i.e., getXyz as xyz
        for (PsiMethod method : clazz.getAllMethods()) {
            if ((suffix == null || method.getName().toLowerCase().startsWith(suffix.toLowerCase()))
                    && !method.isConstructor()
                    && !excludedMethods.contains(method.getName())
                    && !displayValues.contains(method.getName())) {
                displayValues.add(baseExpression + method.getName());
            }
        }
        return displayValues.toArray();
    }


    private String getValueLeftOfCursor() {
        return getElement().getText().substring(this.getRangeInElement().getStartOffset(), Math.min(getElement().getText().indexOf(INTELLIJ_IDEA_RULEZZZ), this.getRangeInElement().getEndOffset()));
    }

    static private final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*)\\b(\\w+)$");
    static private final Pattern LAST_DOT_PATTERN = Pattern.compile("^(.*)\\.\\s*$");
    static private final Pattern LEFT_HAND_EXPRESSION_PATTERN = Pattern.compile("^(.*)\\b(\\w+)(\\(" + parenInnards(6) + "\\))?\\s*$");

    static private String parenInnards(int howManyDeep) {
        if (howManyDeep == 0) {
            return "[^\\)]*";
        } else {
            return "(?:[^\\)]|\\(" + parenInnards(howManyDeep - 1) + "\\))*";
        }
    }


    private PsiClass findMember(String chain) {
        Matcher lastDotMatcher = LAST_DOT_PATTERN.matcher(chain);
        if (!lastDotMatcher.matches()) {
            return psiClass;
        }

        String expression = lastDotMatcher.group(1);
        Matcher leftHandMatcher = LEFT_HAND_EXPRESSION_PATTERN.matcher(expression);
        if (!leftHandMatcher.matches()) {
            // We have an expression to the left of the dot that we can't grok.  Give up tyring to auto-complete.
            return null;
        }
        String leftOfMethodName = leftHandMatcher.group(1);
        String methodName = leftHandMatcher.group(2);
        String possibleParams = (leftHandMatcher.groupCount() == 3) ? leftHandMatcher.group(3) : null;


        PsiClass clazz = findMember(leftOfMethodName);
        if (possibleParams != null && possibleParams.length() > 0) {
            PsiMethod[] possibleMethods = clazz.findMethodsByName(methodName, true);
            //TODO: for now assuming all methods of same name return same type, but later tighten by checking parameter lists
            if (possibleMethods.length > 0) {
                //TODO handle array and list deferencing
                PsiType returnType = possibleMethods[0].getReturnType();
                return resolveTypeToClass(returnType);
            }
        } else {
            PsiField possibleField = clazz.findFieldByName(methodName, true);
            if (possibleField != null) {
                return resolveTypeToClass(possibleField.getType());
            }
            PsiMethod[] possibleGetters = clazz.findMethodsByName(addPrefix("get", methodName), true);
            for (PsiMethod possibleGetter : possibleGetters) {
                if (possibleGetter.getParameterList().getParametersCount() == 0) {
                    return resolveTypeToClass(possibleGetter.getReturnType());
                }
            }
            PsiMethod[] possibleBooleanGetters = clazz.findMethodsByName(addPrefix("is", methodName), true);
            for (PsiMethod possibleBooleanGetter : possibleBooleanGetters) {
                if (possibleBooleanGetter.getParameterList().getParametersCount() == 0) {
                    PsiType returnType = possibleBooleanGetter.getReturnType();
                    if (returnType.isConvertibleFrom(PsiType.BOOLEAN)) {
                        return resolveTypeToClass(returnType);
                    }
                }
            }
        }
        return null;
    }

    private String addPrefix(String prefix, String methodName) {
        return prefix + methodName.substring(0, 1).toUpperCase() + (methodName.length() > 1 ? methodName.substring(2) : "");
    }

    private PsiClass resolveTypeToClass(PsiType returnType) {
        if (returnType instanceof PsiClassType) {
            return ((PsiClassType) returnType).resolve();
        } else {
            return null;
        }
    }


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

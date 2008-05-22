package org.xcordion.ide.intellij;

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.KeywordChooser;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlAttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class XcordionAttributeValueKeywordChooser implements KeywordChooser {
    public static final String[] EMPTY_KEYWORD_LIST = new String[0];
    List<String> excludedMethods = new ArrayList<String>() {
        {
            add("clone");
            add("finalize");
            add("Object");
            add("registerNatives");
        }
    };
    private static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz ";
    static private final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*)\\b(\\w+)$");
    static private final Pattern LAST_DOT_PATTERN = Pattern.compile("^(.*)\\.\\s*$");
    static private final Pattern LEFT_HAND_EXPRESSION_PATTERN = Pattern.compile("^(.*)\\b(\\w+)(\\(" + parenInnards(6) + "\\))?\\s*$");
    static private final Pattern XCORDION_FIELD_NAMES = Pattern.compile("set=\"([#a-zA-Z0-9]+)\"");

    public String[] getKeywords(CompletionContext completionContext, PsiElement psiElement) {
        if (psiElement.getParent() instanceof XmlAttributeValue) {
            XmlAttributeValue attributeValueElement = (XmlAttributeValue) psiElement.getParent();
            String suffix = null;
            String baseExpression = getValueLeftOfCursor(attributeValueElement);
            Matcher suffixMatcher = SUFFIX_PATTERN.matcher(baseExpression);
            if (suffixMatcher.matches()) {
                baseExpression = suffixMatcher.group(1);
                suffix = suffixMatcher.group(2);
            }
            List<String> displayValues = getMethodNameVariants(attributeValueElement, baseExpression, suffix);
            displayValues.addAll(getXcordionFieldNameVariants(attributeValueElement, baseExpression, suffix));
            return displayValues.toArray(new String[0]);
        }
        return EMPTY_KEYWORD_LIST;
    }

    private List<String> getMethodNameVariants(XmlAttributeValue attributeValueElement, String baseExpression, String suffix) {
        List<String> displayValues = new ArrayList<String>();
        if (!baseExpression.endsWith("#")) {
            PsiClass clazz = findMember(baseExpression, attributeValueElement);
            if (clazz != null) {

                //TODO also need to autocomplete on fields and ognl pseudo fields i.e., getXyz as xyz
                for (PsiMethod method : clazz.getAllMethods()) {
                    if ((suffix == null || method.getName().toLowerCase().startsWith(suffix.toLowerCase()))
                            && !method.isConstructor()
                            && !excludedMethods.contains(method.getName())
                            && !displayValues.contains(method.getName())) {
                        displayValues.add(baseExpression + method.getName() + "()");
                    }
                }
            }
        }
        return displayValues;
    }


    private List<String> getXcordionFieldNameVariants(PsiElement attributeValueElement, String baseExpression, String suffix) {
        Matcher matcher = XCORDION_FIELD_NAMES.matcher(attributeValueElement.getContainingFile().getText());
        List<String> displayValues = new ArrayList<String>();
        String prefix;
        if (baseExpression.endsWith("#")) {
            prefix = baseExpression.substring(0, baseExpression.length() - 1);
            suffix = "#" + (suffix==null?"":suffix);
        } else {
            prefix = baseExpression;
        }
        while (matcher.find()) {
            String fieldName = matcher.group(1);
            if (suffix == null || fieldName.startsWith(suffix)) {
                if(prefix.length()==0 && suffix.startsWith("#")){
                    fieldName = fieldName.substring(1);
                }
                displayValues.add(prefix + fieldName);
            }
        }
        return displayValues;
    }


    private String getValueLeftOfCursor(PsiElement psiElement) {
        return psiElement.getText().substring(1, psiElement.getText().indexOf(INTELLIJ_IDEA_RULEZZZ));
    }

    private PsiClass getXcordionTestBackingClass(PsiElement psiElement) {
        //TODO: Look properly at name spaces
        //TODO: Move this check into a filter in the XcordionProject when registering
        if (psiElement instanceof XmlAttributeValue) {
            PsiFile htmlFile = psiElement.getContainingFile().getOriginalFile();
            if (htmlFile == null) {
                htmlFile = psiElement.getOriginalElement().getContainingFile();
            }
            String qualifiedPackageName = htmlFile.getContainingDirectory().getPackage().getQualifiedName();
            //TODO: Align test naming conventions with those used by xcordion
            String className = htmlFile.getName().substring(0, htmlFile.getName().length() - 5) + "Test";
            String qualifiedClassName = qualifiedPackageName + "." + className;
            final PsiClass psiClass = PsiManager.getInstance(psiElement.getProject())
                    .findClass(qualifiedClassName, psiElement.getResolveScope());
            return psiClass;
        }
        return null;
    }

    static private String parenInnards(int howManyDeep) {
        if (howManyDeep == 0) {
            return "[^\\)]*";
        } else {
            return "(?:[^\\)]|\\(" + parenInnards(howManyDeep - 1) + "\\))*";
        }
    }


    private PsiClass findMember(String chain, PsiElement attributeValueElement) {
        Matcher lastDotMatcher = LAST_DOT_PATTERN.matcher(chain);
        if (!lastDotMatcher.matches()) {
            return getXcordionTestBackingClass(attributeValueElement);
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


        PsiClass clazz = findMember(leftOfMethodName, attributeValueElement);
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
        }
        return null;
    }

}

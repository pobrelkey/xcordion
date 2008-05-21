package org.xcordion.ide.intellij;

import com.intellij.ide.DataManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    private static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz ";

    public XcordionReference(XmlAttributeValue element, PsiClass psiClass) {
        super(element);
        this.psiClass = psiClass;
        attributeValue = (XmlAttributeValue) getElement();


    }

    @Nullable
    public PsiElement resolve() {
        PsiMethod[] methods = psiClass.findMethodsByName(getValueLeftOfCursor(), true);
        return methods.length > 0 ? methods[0] : null;
    }

    private String getValueLeftOfCursor() {
        return getElement().getText().substring(0,getElement().getText().indexOf(INTELLIJ_IDEA_RULEZZZ));
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

        for(PsiMethod method:clazz.getAllMethods()){
            if((suffix == null || method.getName().toLowerCase().startsWith(suffix.toLowerCase()))
                    && !method.isConstructor()
                    && !excludedMethods.contains(method.getName())
                    && !displayValues.contains(method.getName())) {
                displayValues.add(method.getName());
            }
        }
        return displayValues.toArray();
    }

    static private final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*)\\b(\\w+)$");
    static private final Pattern LAST_DOT_PATTERN = Pattern.compile("^(.*)\\.\\s*$");
    static private final Pattern LEFT_HAND_EXPRESSION_PATTERN = Pattern.compile("^(.*)\\b(\\w+)(\\(" + parenInnards(6) + "\\))?\\s*$");

    static private String parenInnards(int howManyDeep) {
        if (howManyDeep == 0) {
            return "[^\\)]*";
        } else {
            return "(?:[^\\)]|\\(" + parenInnards(howManyDeep-1) + "\\))*";
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
        if(possibleParams!=null && possibleParams.length()>0){
            PsiMethod[] possibleMethods = clazz.findMethodsByName(methodName, true);
            //TODO: for now assuming all methods of same name return same type, but later tighten by checking parameter lists
            if(possibleMethods.length>0){
                //TODO handle array and list deferencing
                PsiType returnType = possibleMethods[0].getReturnType();
                return resolveTypeToClass(returnType);
            }
        }else{
            PsiField possibleField = clazz.findFieldByName(methodName, true);
            if(possibleField!=null){
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
        return prefix + methodName.substring(0,1).toUpperCase() + (methodName.length() > 1 ? methodName.substring(2) : "");
    }

    private PsiClass resolveTypeToClass(PsiType returnType) {
        if(returnType instanceof PsiClassType){
            return ((PsiClassType) returnType).resolve();
        }else{
            return null;
        }
    }


}

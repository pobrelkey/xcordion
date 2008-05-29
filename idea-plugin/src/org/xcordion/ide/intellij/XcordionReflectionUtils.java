package org.xcordion.ide.intellij;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class XcordionReflectionUtils {
    private static final List<String> EXCLUDED_METHODS = Arrays.asList(
            "clone",
            "finalize",
            "Object",
            "registerNatives"
    );
    private static final Pattern LAST_DOT_PATTERN = Pattern.compile("^(.*)\\.\\s*$");
    private static final Pattern LEFT_HAND_EXPRESSION_PATTERN = Pattern.compile("^(.*)\\b(\\w+)(\\(" + parenInnards(6) + "\\))?\\s*$");
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("(#\\w+)");
    private static final Pattern GETTER_SETTER_PATTERN = Pattern.compile("^([gs]et)([A-Z])(\\w+)$");

    private XcordionReflectionUtils() {
        // static class
    }

    static List<String> getDisplayValues(XmlAttributeValue attributeValueElement, String suffix, String baseExpression) {
        List<String> displayValues = getMethodNameVariants(attributeValueElement, baseExpression, suffix);
        if (!baseExpression.trim().endsWith(".")) {
            displayValues.addAll(getXcordionFieldNameVariants(attributeValueElement, baseExpression, suffix));
        }
        return displayValues;
    }

    static private List<String> getMethodNameVariants(XmlAttributeValue attributeValueElement, String baseExpression, String suffix) {
        TreeSet<String> displayValues = new TreeSet<String>();
        if (!baseExpression.endsWith("#")) {
            PsiClass clazz = findMember(baseExpression, attributeValueElement);
            if (clazz != null) {
                for (PsiMethod method : clazz.getAllMethods()) {
                    // TODO: suffix matching isn't perfect for OGNL getter/setter access - fix this later
                    if ((suffix == null || method.getName().toLowerCase().startsWith(suffix.toLowerCase()))
                            && !method.isConstructor()
                            && !EXCLUDED_METHODS.contains(method.getName())
                            && isPublicMethod(method)) {
                        Matcher m = GETTER_SETTER_PATTERN.matcher(method.getName());
                        if (m.matches() && m.group(1).equals("get") && method.getParameterList().getParametersCount() == 0) {
                            // OGNL getter
                            displayValues.add(baseExpression + m.group(2).toLowerCase() + m.group(3));
                        } else if (m.matches() && m.group(1).equals("set") && method.getParameterList().getParametersCount() == 1) {
                            // OGNL setter
                            displayValues.add(baseExpression + m.group(2).toLowerCase() + m.group(3));
                        } else {
                            displayValues.add(baseExpression + method.getName() + "()");
                        }
                    }
                }
            }
        }
        return new ArrayList<String>(displayValues);
    }

    static private boolean isPublicMethod(PsiMethod method) {
        // TODO: look at method and figure out if we can call it!
        return true;
    }

    static private List<String> getXcordionFieldNameVariants(PsiElement attributeValueElement, String baseExpression, String suffix) {
        XmlFile doc = (XmlFile) attributeValueElement.getContainingFile();
        TreeSet<String> ognlVariableNames = new TreeSet<String>();
        recursivelyScanXcordionTags(ognlVariableNames, doc, attributeValueElement);

        String prefix = baseExpression;
        if (baseExpression.endsWith("#")) {
            prefix = baseExpression.substring(0, baseExpression.length() - 1);
            suffix = "#" + (suffix==null?"":suffix);
        }

        List<String> displayValues = new ArrayList<String>();
        for (String variable : ognlVariableNames) {
            if (suffix == null || variable.startsWith(suffix)) {
                if(prefix.length()==0 && suffix!=null && suffix.startsWith("#")){
                    variable = variable.substring(1);
                }
                displayValues.add(prefix + variable);
            }
        }
        return displayValues;
    }

    static private void recursivelyScanXcordionTags(Set<String> ognlVariableNames, PsiElement element, PsiElement attributeValueElement) {
        for (PsiElement psiChild : element.getChildren()) {
            if (psiChild instanceof XmlTag) {
                XmlTag tag = (XmlTag) psiChild;
                for (XmlAttribute attribute : tag.getAttributes()) {
                    if (XcordionAttribute.isXcordionAttribute(attribute) && attribute.getValueElement() != attributeValueElement) {
                        Matcher matcher = VARIABLE_NAME_PATTERN.matcher(attribute.getValue());
                        while (matcher.find()) {
                            ognlVariableNames.add(matcher.group(1));
                        }
                    }
                }
            }
            recursivelyScanXcordionTags(ognlVariableNames, psiChild, attributeValueElement);
        }
    }

    static private PsiClass getXcordionTestBackingClass(PsiElement psiElement) {
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

    static PsiClass findMember(String chain, PsiElement attributeValueElement) {
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

    static private String addPrefix(String prefix, String methodName) {
        return prefix + methodName.substring(0, 1).toUpperCase() + (methodName.length() > 1 ? methodName.substring(2) : "");
    }

    static private PsiClass resolveTypeToClass(PsiType returnType) {
        if (returnType instanceof PsiClassType) {
            return ((PsiClassType) returnType).resolve();
        }
        return null;
    }

}

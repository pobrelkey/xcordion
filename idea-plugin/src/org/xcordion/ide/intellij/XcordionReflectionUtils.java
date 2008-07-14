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
    private static final Pattern LEFT_HAND_EXPRESSION_PATTERN = Pattern.compile("^(.*)\\b(\\w+)(\\(" + parenInnards(6) + "\\))?(\\[" + bracketInnards(6) + "\\])?\\s*$");
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("(#\\w+)");
    private static final Pattern GETTER_SETTER_PATTERN = Pattern.compile("^(is|[gs]et)([A-Z])(\\w+)$");

    private XcordionReflectionUtils() {
        // static class
    }

    static List<String> getDisplayValues(XmlAttributeValue attributeValueElement, String suffix, String baseExpression) {
        List<String> displayValues = new ArrayList<String>();
        if (!baseExpression.endsWith("#")) {
            displayValues.addAll(getMethodAndFieldNameVariants(attributeValueElement, baseExpression, suffix));
        }
        if (!baseExpression.trim().endsWith(".")) {
            displayValues.addAll(getVariableNameVariants(attributeValueElement, baseExpression, suffix));
        }
        return displayValues;
    }

    static private Collection<String> getMethodAndFieldNameVariants(XmlAttributeValue attributeValueElement, String baseExpression, String suffix) {
        TreeSet<String> displayValues = new TreeSet<String>();
        PsiClass clazz = findMember(baseExpression, attributeValueElement);
        if (clazz != null) {
            for (PsiMethod method : clazz.getAllMethods()) {
                if (!method.isConstructor()
                        && !EXCLUDED_METHODS.contains(method.getName())
                        && isPublic(method)) {
                    Matcher m = GETTER_SETTER_PATTERN.matcher(method.getName());
                    String expression = null;
                    if (m.matches() && (isGetter(method, m) || isBooleanGetter(method, m) || isSetter(method, m))) {
                        // OGNL getter or setter
                        expression = ifMatchesSuffix(suffix, m.group(2).toLowerCase() + m.group(3));
                    }
                    if (expression == null) {
                        // normal method
                        expression = ifMatchesSuffix(suffix, method.getName() + "()");
                    }

                    if (expression != null) {
                        displayValues.add(baseExpression + expression);
                    }
                }
            }
            for (PsiField field : clazz.getAllFields()) {
                if (isPublic(field)) {
                    String expression = ifMatchesSuffix(suffix, field.getName());
                    if (expression != null) {
                        displayValues.add(baseExpression + expression);
                    }
                }
            }
        }
        return displayValues;
    }

    private static boolean isSetter(PsiMethod method, Matcher m) {
        return m.group(1).equals("set") && method.getParameterList().getParametersCount() == 1;
    }

    private static boolean isBooleanGetter(PsiMethod method, Matcher m) {
        return m.group(1).equals("is") && method.getParameterList().getParametersCount() == 0 && method.getReturnType().isConvertibleFrom(PsiType.BOOLEAN);
    }

    private static boolean isGetter(PsiMethod method, Matcher m) {
        return m.group(1).equals("get") && method.getParameterList().getParametersCount() == 0;
    }

    private static String ifMatchesSuffix(String suffix, String expression) {
        return (suffix == null || expression.toLowerCase().startsWith(suffix.toLowerCase())) ? expression : null;
    }

    static private boolean isPublic(PsiModifierListOwner modifiable) {
        return modifiable.getModifierList().hasExplicitModifier("public");
    }

    static private List<String> getVariableNameVariants(PsiElement attributeValueElement, String baseExpression, String suffix) {
        XmlFile doc = (XmlFile) attributeValueElement.getContainingFile();
        TreeSet<String> ognlVariableNames = new TreeSet<String>();
        recursivelyScanXcordionTags(ognlVariableNames, doc, attributeValueElement);
        ognlVariableNames.add("#VALUE");
        ognlVariableNames.add("#HREF");

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
            PsiClass psiClass = findTestClass(psiElement, htmlFile, qualifiedPackageName, "Test");
            if (psiClass == null) {
                psiClass = findTestClass(psiElement, htmlFile, qualifiedPackageName, "");
            }
            return psiClass;
        }
        return null;
    }

    private static PsiClass findTestClass(PsiElement psiElement, PsiFile htmlFile, String qualifiedPackageName, String suffix) {
        String className = htmlFile.getName().substring(0, htmlFile.getName().length() - 5) + suffix;
        String qualifiedClassName = qualifiedPackageName + "." + className;
        PsiClass psiClass = PsiManager.getInstance(psiElement.getProject())
                .findClass(qualifiedClassName, psiElement.getResolveScope());
        return psiClass;
    }

    static private String parenInnards(int howManyDeep) {
        return innards(howManyDeep, "()");
    }

    static private String bracketInnards(int howManyDeep) {
        return innards(howManyDeep, "[]");
    }

    private static String innards(int howManyDeep, String delimiters) {
        if (howManyDeep == 0) {
            return "[^\\" + delimiters.charAt(1) + "]*";
        } else {
            return "(?:[^\\" + delimiters.charAt(1) + "]|\\" + delimiters.charAt(0) + innards(howManyDeep - 1, delimiters) + "\\" + delimiters.charAt(1) + ")*";
        }
    }


    static PsiClass findMember(String chain, PsiElement attributeValueElement) {
        PsiManager psiManager = PsiManager.getInstance(attributeValueElement.getProject());

        Matcher lastDotMatcher = LAST_DOT_PATTERN.matcher(chain);
        if (!lastDotMatcher.matches()) {
            return getXcordionTestBackingClass(attributeValueElement);
        }

        String expression = lastDotMatcher.group(1);
        Matcher leftHandMatcher = LEFT_HAND_EXPRESSION_PATTERN.matcher(expression);
        if (!leftHandMatcher.matches()) {
            // We have an expression to the left of the dot that we can't grok.  Give up trying to auto-complete.
            return null;
        }
        String leftOfMethodName = leftHandMatcher.group(1);
        String methodName = leftHandMatcher.group(2);
        String possibleParams = (leftHandMatcher.groupCount() >= 3) ? leftHandMatcher.group(3) : null;
        // TODO: multi-dimensional arrays/lists
        boolean hasIndexer = (leftHandMatcher.groupCount() >= 4) && (leftHandMatcher.group(4) != null) && (leftHandMatcher.group(4).length() > 0);

        PsiClass clazz = findMember(leftOfMethodName, attributeValueElement);
        if (possibleParams != null && possibleParams.length() > 0) {
            PsiMethod[] possibleMethods = clazz.findMethodsByName(methodName, true);
            if (possibleMethods.length > 0) {
                // First check whether all methods of this name return the same type.
                // (Usually the case - it's good programming practice.  Still, you never know...)
                HashSet<PsiType> returnTypes = new HashSet<PsiType>();
                for (PsiMethod possibleMethod : possibleMethods) {
                    if (isPublic(possibleMethod)) {
                        returnTypes.add(possibleMethod.getReturnType());
                    }
                }
                if (returnTypes.size() == 1) {
                    return resolveTypeToClass(returnTypes.iterator().next(), hasIndexer, psiManager);
                } else if (returnTypes.size() == 0) {
                    // all voids, presumably
                    return null;
                }

                // too many return types - so see if all with same parameter count return same type.
                returnTypes.clear();
                int parametersCount = countParameters(possibleParams);
                for (PsiMethod possibleMethod : possibleMethods) {
                    if (isPublic(possibleMethod) && possibleMethod.getParameterList().getParametersCount() == parametersCount) {
                        returnTypes.add(possibleMethod.getReturnType());
                    }
                }
                if (returnTypes.size() == 1) {
                    return resolveTypeToClass(returnTypes.iterator().next(), hasIndexer, psiManager);
                }

                // give up - test class is horribly written, and figuring out what user wants would require tons o'code
                return null;
            }
        } else {
            PsiField possibleField = clazz.findFieldByName(methodName, true);
            if (possibleField != null && isPublic(possibleField)) {
                return resolveTypeToClass(possibleField.getType(), hasIndexer, psiManager);
            }
            PsiMethod[] possibleGetters = clazz.findMethodsByName(addPrefix("get", methodName), true);
            for (PsiMethod possibleGetter : possibleGetters) {
                if (isPublic(possibleGetter) && possibleGetter.getParameterList().getParametersCount() == 0) {
                    return resolveTypeToClass(possibleGetter.getReturnType(), hasIndexer, psiManager);
                }
            }
            PsiMethod[] possibleBooleanGetters = clazz.findMethodsByName(addPrefix("is", methodName), true);
            for (PsiMethod possibleBooleanGetter : possibleBooleanGetters) {
                if (isPublic(possibleBooleanGetter) && possibleBooleanGetter.getParameterList().getParametersCount() == 0) {
                    PsiType returnType = possibleBooleanGetter.getReturnType();
                    if (returnType.isConvertibleFrom(PsiType.BOOLEAN)) {
                        return resolveTypeToClass(returnType, hasIndexer, psiManager);
                    }
                }
            }
        }
        return null;
    }

    static private final Pattern PARAMETER_PATTERN = Pattern.compile("([^,]|\\(" + parenInnards(6) + "\\))+");
    private static int countParameters(String possibleParams) {
        int result = 0;
        Matcher m = PARAMETER_PATTERN.matcher(possibleParams.substring(1, possibleParams.length() - 1));
        while (m.find()) {
            result++;
        }
        return result;
    }

    static private String addPrefix(String prefix, String methodName) {
        return prefix + methodName.substring(0, 1).toUpperCase() + (methodName.length() > 1 ? methodName.substring(1) : "");
    }

    static private PsiClass resolveTypeToClass(PsiType returnType, boolean hasIndexer, PsiManager psiManager) {
        if (returnType instanceof PsiArrayType) {
            PsiType componentType = ((PsiArrayType) returnType).getComponentType();
            if (componentType instanceof PsiClassType) {
                return ((PsiClassType) componentType).resolve();
            }
            // multi-dimensional array, presumably
            return null;
        } else if (returnType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) returnType;
            PsiClass clazz = classType.resolve();
            if (!hasIndexer) {
                return clazz;
            }
            PsiClass listClazz = psiManager.findClass("java.util.List");
            if ((listClazz.equals(clazz) || clazz.isInheritor(listClazz, true)) && classType.hasNonTrivialParameters()) {
                PsiClassType.ClassResolveResult classResolveResult = classType.resolveGenerics();
                if (classResolveResult.isValidResult()) {
                    PsiType componentType = classResolveResult.getSubstitutor().substitute(classResolveResult.getElement().getTypeParameters()[0]);
                    if (componentType instanceof PsiClassType) {
                        return ((PsiClassType) componentType).resolve();
                    }
                }
            }
            // we have an indexer, but class isn't a generic list, so we don't know element type - give up
            return null;
        }
        return null;
    }

}
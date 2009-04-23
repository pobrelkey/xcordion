package org.xcordion.ide.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import static com.intellij.psi.search.GlobalSearchScope.allScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class XcordionReflectionUtils {
    private static final List<String> EXCLUDED_METHODS = Arrays.asList(
            "clone",
            "finalize",
            "Object",
            "registerNatives"
    );
    private static final Pattern LAST_DOT_PATTERN = Pattern.compile("^(.*)\\.\\s*$", Pattern.DOTALL);
    private static final Pattern LEFT_HAND_EXPRESSION_PATTERN = Pattern.compile("^(.*)\\b(\\w+)(\\(" + parenInnards(6) + "\\))?(\\[" + bracketInnards(6) + "\\])?\\s*$", Pattern.DOTALL);
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("(#\\w+)");
    private static final Pattern GETTER_SETTER_PATTERN = Pattern.compile("^(is|[gs]et)([A-Z])(\\w+)$");
    private static final String ENDS_WITH_HASH = "^(.*)\\n(\\s*.*)*#";

    private XcordionReflectionUtils() {
        // static class
    }

    static List<AutoCompleteItem> getAutoCompleteItems(XmlAttributeValue attributeValueElement, String suffix, String baseExpression) {
        List<AutoCompleteItem> displayValues = new ArrayList<AutoCompleteItem>();
        if (!baseExpression.endsWith("#")) {
            displayValues.addAll(getMethodAndFieldNameVariants(attributeValueElement, baseExpression, suffix));
        }

//        if (StringUtils.isBlank(baseExpression.trim()) || baseExpression.trim().matches(ENDS_WITH_HASH) || baseExpression.trim().endsWith(",")) {
//            displayValues.addAll(getVariableNameVariants(attributeValueElement, baseExpression, suffix));
//        }

        return displayValues;
    }

    private static Collection<AutoCompleteItem> getMethodAndFieldNameVariants(XmlAttributeValue attributeValueElement, String baseExpression, String suffix) {
        List<AutoCompleteItem> displayValues = new ArrayList<AutoCompleteItem>();
        PsiClass clazz = findMember(baseExpression, attributeValueElement);

        if (clazz != null) {
            displayValues.addAll(getMethodsToDisplay(clazz, suffix));
            displayValues.addAll(getFieldsToDisplay(clazz, suffix));
        }
        return displayValues;
    }

    private static List<AutoCompleteItem> getFieldsToDisplay(PsiClass clazz, String suffix) {
        List<AutoCompleteItem> fields = new ArrayList<AutoCompleteItem>();

        for (PsiField field : clazz.getAllFields()) {
            if (isPublic(field)) {
                String expression = ifMatchesSuffix(suffix, field.getName());
                if (expression != null) {
                    fields.add(new AutoCompleteItem(expression, field.getType().getPresentableText()));
                }
            }
        }
        return fields;
    }

    private static List<AutoCompleteItem> getMethodsToDisplay(PsiClass clazz, String suffix) {
        List<AutoCompleteItem> methods = new ArrayList<AutoCompleteItem>();

        for (PsiMethod method : clazz.getAllMethods()) {
            if (isPotentialMethod(method)) {
                Matcher m = GETTER_SETTER_PATTERN.matcher(method.getName());
                String expression = null;
                if (m.matches() && (isGetter(method, m) || isBooleanGetter(method, m) || isSetter(method, m))) {
                    // OGNL getter or setter
                    expression = ifMatchesSuffix(suffix, m.group(2).toLowerCase() + m.group(3));
                }
                if (expression == null) {
                    // normal method
                    expression = ifMatchesSuffix(suffix, method.getName() + getParameterString(method));
                }
                if (expression != null) {
                    methods.add(new AutoCompleteItem(expression, method.getReturnType().getPresentableText()));
                }
            }
        }
        return methods;
    }

    private static String getParameterString(PsiMethod method) {
        String parameterString = "(";
        PsiParameter[] parameters = method.getParameterList().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            PsiParameter parameter = parameters[i];
            parameterString += parameter.getType().getPresentableText();
            if (i != parameters.length - 1) {
                parameterString += ", ";
            }
        }
        parameterString += ")";
        return parameterString;
    }

    private static boolean isPotentialMethod(PsiMethod method) {
        return !method.isConstructor()
                && !EXCLUDED_METHODS.contains(method.getName())
                && isPublic(method);
    }

    private static boolean isSetter(PsiMethod method, Matcher m) {
        return m.group(1).equals("set") && method.getParameterList().getParametersCount() == 1;
    }

    private static boolean isBooleanGetter(PsiMethod method, Matcher m) {
        return m.group(1).equals("is") && method.getParameterList().getParametersCount() == 0
                && method.getReturnType().isConvertibleFrom(PsiType.BOOLEAN);
    }

    private static boolean isGetter(PsiMethod method, Matcher m) {
        return m.group(1).equals("get") && method.getParameterList().getParametersCount() == 0;
    }

    private static String ifMatchesSuffix(String suffix, String expression) {
        return (suffix == null || expression.toLowerCase().startsWith(suffix.toLowerCase())) ? expression : null;
    }

    private static boolean isPublic(PsiModifierListOwner modifiable) {
        return modifiable.getModifierList().hasExplicitModifier("public");
    }

    private static List<AutoCompleteItem> getVariableNameVariants(PsiElement attributeValueElement, String baseExpression, String suffix) {
        XmlFile doc = (XmlFile) attributeValueElement.getContainingFile();
        TreeSet<String> ognlVariableNames = new TreeSet<String>();
        recursivelyScanXcordionTags(ognlVariableNames, doc, attributeValueElement);
        ognlVariableNames.add("#VALUE");
        ognlVariableNames.add("#HREF");

        String prefix = baseExpression;
        if (baseExpression.endsWith("#")) {
            prefix = baseExpression.substring(0, baseExpression.length() - 1);
            suffix = "#" + (suffix == null ? "" : suffix);
        }

        List<AutoCompleteItem> displayValues = new ArrayList<AutoCompleteItem>();
        for (String variable : ognlVariableNames) {
            if (suffix == null || variable.startsWith(suffix)) {
                if (prefix.length() == 0 && suffix != null && suffix.startsWith("#")) {
                    variable = variable.substring(1);
                }
                displayValues.add(new AutoCompleteItem(variable, "html variable"));
            }
        }
        return displayValues;
    }

    private static void recursivelyScanXcordionTags(Set<String> ognlVariableNames, PsiElement element, PsiElement attributeValueElement) {
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

    private static PsiClass getXcordionTestBackingClass(PsiElement psiElement) {
        if (psiElement instanceof XmlAttributeValue) {
            HtmlFileImpl htmlFile = getHtmlFile(psiElement);
            String qualifiedPackageName = getQualifiedPackageName(htmlFile);

            PsiClass psiClass = findTestClass(psiElement, htmlFile, qualifiedPackageName, "Test");
            if (psiClass == null) {
                psiClass = findTestClass(psiElement, htmlFile, qualifiedPackageName, "");
            }
            return psiClass;
        }
        return null;
    }

    private static String getQualifiedPackageName(HtmlFileImpl htmlFile) {
        return JavaDirectoryService.getInstance().getPackage(htmlFile.getContainingDirectory()).getQualifiedName();
    }

    private static HtmlFileImpl getHtmlFile(PsiElement psiElement) {
        HtmlFileImpl htmlFile = (HtmlFileImpl) psiElement.getContainingFile().getOriginalFile();
        if (htmlFile == null) {
            htmlFile = (HtmlFileImpl) psiElement.getOriginalElement().getContainingFile();
        }
        return htmlFile;
    }

    private static PsiClass findTestClass(PsiElement psiElement, PsiFile htmlFile, String qualifiedPackageName, String suffix) {
        String className = htmlFile.getName().replace(".html", "") + suffix;
        String qualifiedClassName = getQualifiedClassName(qualifiedPackageName, className);
        return JavaPsiFacade.getInstance(psiElement.getProject()).findClass(qualifiedClassName, psiElement.getResolveScope());
    }

    private static String getQualifiedClassName(String qualifiedPackageName, String className) {
        String qualifiedClassName;
        if (StringUtils.isBlank(qualifiedPackageName)) {
            qualifiedClassName = className;
        } else {
            qualifiedClassName = qualifiedPackageName + "." + className;
        }
        return qualifiedClassName;
    }

    private static String parenInnards(int howManyDeep) {
        return innards(howManyDeep, "()");
    }

    private static String bracketInnards(int howManyDeep) {
        return innards(howManyDeep, "[]");
    }

    private static String innards(int howManyDeep, String delimiters) {
        if (howManyDeep == 0) {
            return "[^\\" + delimiters.charAt(1) + "]*";
        } else {
            return "(?:[^\\" + delimiters.charAt(1) + "]|\\" + delimiters.charAt(0) + innards(howManyDeep - 1, delimiters) + "\\" + delimiters.charAt(1) + ")*";
        }
    }

    private static PsiClass findMember(String chain, PsiElement attributeValueElement) {
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
        if (clazz != null) {
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
        }
        return null;
    }

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("([^,]|\\(" + parenInnards(6) + "\\))+");

    private static int countParameters(String possibleParams) {
        int result = 0;
        Matcher m = PARAMETER_PATTERN.matcher(possibleParams.substring(1, possibleParams.length() - 1));
        while (m.find()) {
            result++;
        }
        return result;
    }

    private static String addPrefix(String prefix, String methodName) {
        return prefix + methodName.substring(0, 1).toUpperCase() + (methodName.length() > 1 ? methodName.substring(1) : "");
    }

    private static PsiClass resolveTypeToClass(PsiType returnType, boolean hasIndexer, PsiManager psiManager) {
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
            Project project = psiManager.getProject();
            PsiClass listClazz = JavaPsiFacade.getInstance(project).findClass("java.util.List", allScope(project));
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

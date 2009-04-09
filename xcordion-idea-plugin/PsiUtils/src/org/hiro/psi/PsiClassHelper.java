package org.hiro.psi;

import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.psi.*;
import static jedi.functional.Coercions.asArray;
import static jedi.functional.Coercions.asList;
import jedi.functional.Filter;
import static jedi.functional.FirstOrderLogic.and;
import static jedi.functional.FunctionalPrimitives.collect;
import static jedi.functional.FunctionalPrimitives.select;
import jedi.functional.Functor;
import static org.apache.commons.lang.StringUtils.capitalize;

import java.util.List;

public class PsiClassHelper {
    private PsiClassHelper() {
    }

    public static PsiElementClassMember[] getAllFieldsInHierarchyToDisplay(PsiClass clazz) {
        List<PsiField> fieldList = asList(clazz.getAllFields());
        List<PsiField> filteredFields = select(fieldList, and(isNotFinal(), isNotAnEnumConstant()));

        return createPsiFieldMembers(filteredFields);
    }

    public static PsiElementClassMember[] getAllAccessibleFieldsInHierarchyToDisplay(PsiClass clazz) {
        List<PsiField> localFields = asList(clazz.getFields());
        List<PsiField> allFields = asList(clazz.getAllFields());
        List<PsiMethod> methodList = asList(clazz.getAllMethods());
        List<PsiField> filteredFields = select(allFields, and(hasNoExistingWithMethod(methodList), isNotFinal(), isNotAnEnumConstant(), isAccessible(localFields)));

        return createPsiFieldMembers(filteredFields);
    }

    private static PsiElementClassMember[] createPsiFieldMembers(List<PsiField> filteredFields) {
        List<PsiElementClassMember> classMembers = collect(filteredFields, new Functor<PsiField, PsiElementClassMember>() {
            public PsiElementClassMember execute(PsiField psiField) {
                return new PsiFieldMember(psiField);
            }
        });
        return classMembers.isEmpty() ? new PsiElementClassMember[0] : asArray(classMembers);
    }

    private static Filter<PsiField> isNotAnEnumConstant() {
        return new Filter<PsiField>() {
            public Boolean execute(PsiField psiField) {
                return !(psiField instanceof PsiEnumConstant);
            }
        };
    }

    private static Filter<PsiField> isNotFinal() {
        return new Filter<PsiField>() {
            public Boolean execute(PsiField psiField) {
                PsiModifierList modifierList = psiField.getModifierList();
                return modifierList == null || !modifierList.hasExplicitModifier("final");
            }
        };
    }

    private static Filter<PsiField> hasNoExistingWithMethod(final List<PsiMethod> methodList) {
        return new Filter<PsiField>() {
            public Boolean execute(PsiField psiField) {
                for (PsiMethod psiMethod : methodList) {
                    if (psiMethod.getName().equals("with" + capitalize(psiField.getName()))) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private static Filter<PsiField> isAccessible(final List<PsiField> fields) {
        return new Filter<PsiField>() {
            public Boolean execute(PsiField psiField) {
                PsiModifierList modifierList = psiField.getModifierList();

                return fields.contains(psiField) || modifierList == null || !modifierList.hasExplicitModifier("private");
            }
        };
    }
}
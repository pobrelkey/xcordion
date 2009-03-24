package org.hiro.psi;

import com.intellij.openapi.project.Project;
import static com.intellij.openapi.ui.Messages.showInfoMessage;
import com.intellij.psi.*;
import static com.intellij.psi.JavaPsiFacade.getInstance;
import com.intellij.psi.search.GlobalSearchScope;
import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked"})
public class BasePsiClassBuilder<T extends BasePsiClassBuilder> implements PsiClassBuilder<T, PsiClass> {
    private final PsiElementFactory elementFactory;
    private final Project project;

    private final List<String> fields = new ArrayList<String>();
    private final List<PsiMethod> methods = new ArrayList<PsiMethod>();
    private final List<PsiImportStatement> importStatements = new ArrayList<PsiImportStatement>();
    private PsiDirectory directory;

    String className;
    String qualifiedClassName;
    String superClassName;

    public BasePsiClassBuilder(PsiElementFactory elementFactory, Project project) {
        this.elementFactory = elementFactory;
        this.project = project;
    }

    public PsiElementFactory getElementFactory() {
        return elementFactory;
    }

    public PsiClass build() {
        PsiClass psiClass = JavaDirectoryService.getInstance().createClass(directory, className);

        PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
        PsiImportList importList = javaFile.getImportList();

        for (PsiImportStatement importStatement : importStatements) {
            importList.add(importStatement);
        }

        for (String fieldText : fields) {
            psiClass.add(elementFactory.createFieldFromText(fieldText, psiClass.getOriginalElement()));
        }

        for (PsiMethod method : methods) {
            psiClass.add(method);
        }

        if (isNotBlank(superClassName)) {
            addSuperClass(psiClass);
        }

        return psiClass;
    }

    private void addSuperClass(PsiClass psiClass) {
        PsiClass superClass = getSuperClass();

        if (superClass == null) {
            showInfoMessage("Invalid super class [" + superClassName + "], please check your settings", "Invalid Super Class");
        } else {
            PsiReferenceList extendsList = psiClass.getExtendsList();
            extendsList.add(elementFactory.createKeyword("extends"));
            extendsList.add(elementFactory.createReferenceExpression(superClass));
        }
    }

    private PsiClass getSuperClass() {
        JavaPsiFacade psiFacade = getInstance(project);
        GlobalSearchScope globalSearchScope = allScope(project);
        return psiFacade.findClass(superClassName, globalSearchScope);
    }

    public T withName(String className) {
        this.className = className;
        return (T) this;
    }

    public T withQualifiedName(String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
        return (T) this;
    }

    public T withDirectory(PsiDirectory directory) {
        this.directory = directory;
        return (T) this;
    }

    public T withImports(List<PsiImportStatement> importStatements) {
        if (importStatements != null) {
            this.importStatements.addAll(importStatements);
        }
        return (T) this;
    }

    public T withField(String field) {
        fields.add(field);
        return (T) this;
    }

    public T withMethod(PsiMethod newMethod) {
        this.methods.add(newMethod);
        return (T) this;
    }

    public T withMethods(List<PsiMethod> methods) {
        this.methods.addAll(methods);
        return (T) this;
    }

    public T withSuperClass(String qualifiedSuperClassName) {
        this.superClassName = qualifiedSuperClassName;
        return (T) this;
    }
}

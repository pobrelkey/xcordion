package org.hiro.psi;

import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked"})
public class BasePsiClassBuilder<T extends BasePsiClassBuilder> implements PsiClassBuilder<T, PsiClass> {
    private final PsiElementFactory elementFactory;

    private PsiDirectory directory;
    private List<PsiImportStatement> importStatements = new ArrayList<PsiImportStatement>();
    private List<String> fields = new ArrayList<String>();
    private List<PsiMethod> methods = new ArrayList<PsiMethod>();

    String className;
    String qualifiedClassName;
    String superClassName;

    public BasePsiClassBuilder(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
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

        PsiReferenceList extendsList = psiClass.getExtendsList();
        extendsList.add(elementFactory.createKeyword("extends"));
        extendsList.add(elementFactory.createReferenceElementByFQClassName(superClassName, GlobalSearchScope.allScope(directory.getProject())));

        return psiClass;
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
            this.importStatements = importStatements;
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

    public T withSuperClass(String qualifiedClassName) {
        this.superClassName = qualifiedClassName;
        return (T) this;
    }
}

package org.hiro.psi;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ex.ProjectRootManagerEx;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.JavaPsiFacadeImpl;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.messages.impl.MessageBusImpl;

public class PsiHelper {

    private PsiElementFactory elementFactory;
    private final Project project;

    public PsiHelper(Project project) {
        this.project = project;
    }

    public PsiHelper(DataContext dataContext) {
        project = DataKeys.PROJECT.getData(dataContext);
    }

    public Project getProject() {
        return project;
    }

    public Editor getEditor() {
        return FileEditorManager.getInstance(getProject()).getSelectedTextEditor();
    }

    public PsiElement findElementAtCursorPosition() {
        return getSelectedJavaFile().findElementAt(getEditor().getCaretModel().getOffset());
    }

    public PsiJavaFile getSelectedJavaFile() {
        PsiFile psiFile = getCurrentFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return null;
        } else {
            return (PsiJavaFile) psiFile;
        }
    }

    public PsiFile getCurrentFile() {
        Editor editor = getEditor();
        if (editor == null) {
            return null;
        }

        VirtualFile vFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        return getPsiManager().findFile(vFile);
    }

    public PsiJavaFile getCurrentJavaFile() {
        return (PsiJavaFile) getCurrentFile();
    }

    private PsiElement getPsiElement() {
        return getCurrentJavaFile().findElementAt(getEditor().getCaretModel().getOffset());
    }

    public PsiElementFactory getElementFactory() {
        if (elementFactory == null) {
            elementFactory = new JavaPsiFacadeImpl(getProject(), (PsiManagerImpl) PsiManager.getInstance(getProject()), ProjectRootManagerEx.getInstanceEx(getProject()),
                    StartupManager.getInstance(getProject()), new MessageBusImpl()).getElementFactory();
        }
        return elementFactory;
    }

    public CodeStyleManager getCodeStyleManager() {
        return getPsiManager().getCodeStyleManager();
    }

    public PsiElement getCurrentElement() {
        PsiElement psiElement = getPsiElement();
        return (psiElement != null) ? psiElement : getCurrentElementFromEditor();
    }

    public PsiClass getCurrentClass() {
        return findClass(getCurrentElement());
    }

    public PsiManager getPsiManager() {
        return PsiManager.getInstance(getProject());
    }

    private PsiElement getCurrentElementFromEditor() {
        Editor editor = getEditor();
        PsiFile psiFile = getCurrentFile();
        if (editor != null && psiFile != null) {
            return psiFile.findElementAt(editor.getCaretModel().getOffset());
        }
        return null;
    }

    public PsiClass findClass(PsiElement element) {
        PsiClass psiClass = (element instanceof PsiClass) ? (PsiClass) element : PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (psiClass != null && psiClass.getContainingClass() instanceof PsiAnonymousClass) {
            return findClass(psiClass.getParent());
        }
        return psiClass;
    }

    public PsiMethod findMethodByName(PsiClass clazz, String name) {
        PsiMethod[] methods = clazz.getMethods();

        for (PsiMethod method : methods) {
            if (name.equals(method.getName()))
                return method;
        }
        return null;
    }

    public static PsiMethod findParentMethod(PsiElement elem) {
        if (elem == null) {
            return null;
        } else if (elem instanceof PsiMethod) {
            return (PsiMethod) elem;
        }
        return findParentMethod(elem.getParent());
    }
}


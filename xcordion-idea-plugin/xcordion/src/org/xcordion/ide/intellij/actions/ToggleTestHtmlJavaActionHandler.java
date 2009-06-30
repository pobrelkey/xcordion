package org.xcordion.ide.intellij.actions;

import static com.intellij.ide.util.EditSourceUtil.getDescriptor;
import com.intellij.openapi.actionSystem.DataContext;
import static com.intellij.openapi.application.ApplicationManager.getApplication;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.module.Module;
import static com.intellij.openapi.ui.Messages.getQuestionIcon;
import static com.intellij.openapi.ui.Messages.showOkCancelDialog;
import com.intellij.openapi.util.Computable;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import static com.intellij.psi.JavaPsiFacade.getInstance;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import static com.intellij.psi.search.ProjectScope.getProjectScope;
import static jedi.functional.Coercions.asList;
import jedi.functional.Filter;
import static jedi.functional.FirstOrderLogic.exists;
import org.hiro.psi.BasePsiClassBuilder;
import org.hiro.psi.PsiHelper;
import org.hiro.psi.PsiMethodBuilder;
import org.xcordion.ide.intellij.XcordionNamespace;
import static org.xcordion.ide.intellij.settings.XcordionSettingsContext.getModuleSettings;

class ToggleTestHtmlJavaActionHandler extends EditorActionHandler {
    private static final String XCORDION_TEST_CASE = "XcordionTestCase";
    private static final String ABSTRACT_XCORDION_TEST_CASE = "AbstractXcordionTestCase";
    private static final String HTML_FILE_EXTENSION = ".html";
    private static final String TEST = "Test";

    private PsiHelper psiHelper;
    private Module module;
    private PsiDirectory psiDirectory;
    private PsiFile currentFile;
    private GlobalSearchScope globalSearchScope;
    private PsiPackage psiPackage;
    private String fileNameWithoutExtension;

    public void execute(Editor editor, DataContext dataContext) {
        psiHelper = new PsiHelper(dataContext);
        module = psiHelper.getModule(dataContext);
        currentFile = psiHelper.getCurrentFile();
        fileNameWithoutExtension = currentFile.getVirtualFile().getNameWithoutExtension();
        psiDirectory = currentFile.getContainingDirectory();
        psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
        globalSearchScope = getProjectScope(psiHelper.getProject());

        if (validXcordionHtmlFile()) {
            toggleHtmlJavaFile(getXcordionBackingClass());
        } else if (validXcordionBackingClass()) {
            toggleHtmlJavaFile(getXcordionHtmlFile());
        }
    }

    private PsiElement getXcordionBackingClass() {
        PsiElement psiTestClass;
        String testClassName = getBackingClassName();
        psiTestClass = getInstance(psiHelper.getProject()).findClass(getFullyQualifiedPackageName() + testClassName, globalSearchScope);

        if (psiTestClass == null) {
            if (okToCreateTestClass()) {
                psiTestClass = new CreateClassCommand(psiHelper.getElementFactory(), psiDirectory, testClassName).compute();
                psiHelper.getCodeStyleManager().reformat(psiTestClass.getContainingFile());
            }
        }
        return psiTestClass;
    }

    private void toggleHtmlJavaFile(PsiElement psiElement) {
        Navigatable navigatable = getDescriptor(psiElement);
        if (navigatable != null) {
            navigatable.navigate(true);
        }
    }

    private boolean okToCreateTestClass() {
        return !getModuleSettings(module).showConfirmationMessage() ||
                showOkCancelDialog("Would you like to create a test class for this file?", "Test class not found", getQuestionIcon()) == 0;
    }

    private boolean validXcordionHtmlFile() {
        String fileContents = currentFile.getText();
        return currentFile instanceof HtmlFileImpl &&
                (fileContents.contains(XcordionNamespace.NAMESPACE_XCORDION.getNamespace())
                        || fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_2007.getNamespace())
                        || fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_ANCIENT.getNamespace())
                        || fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_OLD.getNamespace()));
    }

    private boolean validXcordionBackingClass() {
        return currentFile instanceof PsiJavaFile && isXcordionTestCase();
    }

    private boolean isXcordionTestCase() {
        String fullyQualifiedClassName = getFullyQualifiedPackageName() + fileNameWithoutExtension;
        PsiClass currentClass = getInstance(psiHelper.getProject()).findClass(fullyQualifiedClassName, globalSearchScope);
        PsiClassType[] superClassTypes = currentClass.getExtendsListTypes();
        return superClassTypes.length > 0 && xcordionTestCaseIsInHierarchy(superClassTypes[0], getXcordionTestCaseName());
    }

    private static boolean xcordionTestCaseIsInHierarchy(PsiClassType psiClassType, String xcordionTestCaseName) {
        return classNameMatches(psiClassType.getClassName(), xcordionTestCaseName)
                || exists(asList(psiClassType.getSuperTypes()), xcordionTestCaseFilter(xcordionTestCaseName));
    }

    private static Filter<PsiType> xcordionTestCaseFilter(final String xcordionTestCaseName) {
        return new Filter<PsiType>() {
            public Boolean execute(PsiType psiType) {
                if (psiType instanceof PsiClassType) {
                    PsiClassType psiClassType = (PsiClassType) psiType;

                    return classNameMatches(psiClassType.getClassName(), xcordionTestCaseName)
                            || xcordionTestCaseIsInHierarchy(psiClassType, xcordionTestCaseName);
                }
                return false;
            }
        };
    }

    private static boolean classNameMatches(String className, String xcordionTestCaseName) {
        return XCORDION_TEST_CASE.equals(className)
                || ABSTRACT_XCORDION_TEST_CASE.equals(className)
                || xcordionTestCaseName.equals(className);
    }

    private String getBackingClassName() {
        return (fileNameWithoutExtension.endsWith(TEST)) ? fileNameWithoutExtension : fileNameWithoutExtension + TEST;
    }

    private PsiFile getXcordionHtmlFile() {
        String htmlFileName = fileNameWithoutExtension.substring(0, fileNameWithoutExtension.length() - 4) + HTML_FILE_EXTENSION;
        PsiFile psiFile = psiDirectory.findFile(htmlFileName);
        return (psiFile == null) ? psiDirectory.findFile(fileNameWithoutExtension + HTML_FILE_EXTENSION) : psiFile;
    }

    private class CreateClassCommand implements Computable<PsiClass> {
        private final PsiElementFactory elementFactory;
        private final PsiDirectory psiDirectory;
        private final String testClassName;

        CreateClassCommand(PsiElementFactory elementFactory, PsiDirectory psiDirectory, String testClassName) {
            this.elementFactory = elementFactory;
            this.psiDirectory = psiDirectory;
            this.testClassName = testClassName;
        }

        public PsiClass compute() {
            return getApplication().runWriteAction(new Computable<PsiClass>() {
                public PsiClass compute() {
                    return new BasePsiClassBuilder(elementFactory, psiHelper.getProject())
                            .withDirectory(psiDirectory)
                            .withName(testClassName)
                            .withMethod(new PsiMethodBuilder(elementFactory, psiHelper.getCodeStyleManager())
                                    .withMethodText("public boolean isExpectedToPass() {return false;}")
                                    .build())
                            .withSuperClass(getFullyQualifiedXcordionTestCaseName())
                            .build();
                }
            });
        }
    }

    private String getFullyQualifiedPackageName() {
        String packageName = psiPackage.getQualifiedName();
        if (packageName.length() > 0) {
            packageName += ".";
        }
        return packageName;
    }

    private String getFullyQualifiedXcordionTestCaseName() {
        return getModuleSettings(module).getXcordionBackingClassName();
    }

    private String getXcordionTestCaseName() {
        String testCaseName = getFullyQualifiedXcordionTestCaseName();
        return testCaseName.substring(testCaseName.lastIndexOf(".") + 1);
    }
}

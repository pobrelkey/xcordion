package org.xcordion.ide.intellij.actions;

import static com.intellij.ide.util.EditSourceUtil.getDescriptor;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import static com.intellij.openapi.application.ApplicationManager.getApplication;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Computable;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import static com.intellij.psi.JavaPsiFacade.getInstance;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import static com.intellij.psi.search.ProjectScope.getProjectScope;
import static jedi.functional.Coercions.asList;
import jedi.functional.Filter;
import static jedi.functional.FunctionalPrimitives.select;
import org.hiro.psi.BasePsiClassBuilder;
import org.hiro.psi.PsiHelper;
import org.hiro.psi.PsiMethodBuilder;
import org.xcordion.ide.intellij.XcordionNamespace;
import static org.xcordion.ide.intellij.settings.XcordionSettingsContext.getConfiguration;

public class ToggleTestHtmlJavaActionHandler extends EditorActionHandler {
    private static final String XCORDION_TEST_CASE = "XcordionTestCase";
    private static final String HTML_FILE_EXTENSION = ".html";

    private PsiHelper psiHelper;
    private String moduleName;
    private PsiDirectory psiDirectory;
    private PsiFile currentFile;
    private GlobalSearchScope projectScope;
    private PsiPackage psiPackage;
    private String fileNameWithoutExtension;

    public void execute(Editor editor, DataContext dataContext) {
        psiHelper = new PsiHelper(dataContext);
        moduleName = DataKeys.MODULE.getData(dataContext).getName();
        currentFile = psiHelper.getCurrentFile();
        fileNameWithoutExtension = currentFile.getVirtualFile().getNameWithoutExtension();
        psiDirectory = currentFile.getContainingDirectory();
        psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
        projectScope = getProjectScope(psiHelper.getProject());

        if (isValidXcordionHtmlFile()) {
            String testClassName = getBackingClassName();
            PsiClass psiTestClass = findBackingClassIfExists(testClassName);

            if (psiTestClass == null) {
                psiTestClass = new CreateClassCommand(psiHelper.getElementFactory(), psiDirectory, testClassName).compute();
                psiHelper.getCodeStyleManager().reformat(psiTestClass.getContainingFile());
            }
            toggleHtmlJavaFile(psiTestClass);

        } else if (isValidXcordionBackingClass()) {
            toggleHtmlJavaFile(getXcordionHtmlFile());
        }
    }

    private boolean isValidXcordionHtmlFile() {
        String fileContents = currentFile.getText();
        return currentFile instanceof HtmlFileImpl &&
                (fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_2007.getNamespace()) ||
                        fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_ANCIENT.getNamespace()) ||
                        fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_OLD.getNamespace()));
    }

    private boolean isValidXcordionBackingClass() {
        return currentFile instanceof PsiJavaFile && isXcordionTestCase();
    }

    private boolean isXcordionTestCase() {
        String fqClassName = getFullQualifiedPackageName() + fileNameWithoutExtension;
        PsiClass currentClass = getInstance(psiHelper.getProject()).findClass(fqClassName, projectScope);
        return select(asList(currentClass.getExtendsList().getReferencedTypes()), xcordionTestCaseFilter(getXcordionTestCaseName())).size() > 0;
    }

    private PsiClass findBackingClassIfExists(String testClassName) {
        return getInstance(psiHelper.getProject()).findClass(getFullQualifiedPackageName() + testClassName, projectScope);
    }

    private String getBackingClassName() {
        return (fileNameWithoutExtension.endsWith("Test")) ? fileNameWithoutExtension : fileNameWithoutExtension + "Test";
    }

    private PsiFile getXcordionHtmlFile() {
        String htmlFileName = fileNameWithoutExtension.substring(0, fileNameWithoutExtension.length() - 4) + HTML_FILE_EXTENSION;
        PsiFile psiFile = psiDirectory.findFile(htmlFileName);
        return (psiFile == null) ? psiDirectory.findFile(fileNameWithoutExtension + HTML_FILE_EXTENSION) : psiFile;
    }

    private void toggleHtmlJavaFile(PsiElement psiElement) {
        Navigatable navigatable = getDescriptor(psiElement);
        if (navigatable != null) {
            navigatable.navigate(true);
        }
    }

    private static Filter<PsiClassType> xcordionTestCaseFilter(final String xcordionTestCaseName) {
        return new Filter<PsiClassType>() {
            public Boolean execute(PsiClassType psiClassType) {
                return XCORDION_TEST_CASE.equals(psiClassType.getClassName()) || xcordionTestCaseName.equals(psiClassType.getClassName());
            }
        };
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
                    return new BasePsiClassBuilder(elementFactory)
                            .withDirectory(psiDirectory)
                            .withName(testClassName)
                            .withMethod(new PsiMethodBuilder(elementFactory, psiHelper.getCodeStyleManager())
                                    .withMethodText("public boolean isExpectedToPass() {return false;}")
                                    .build())
                            .withSuperClass(getXcordionTestCaseName())
                            .build();
                }
            });
        }
    }

    private String getFullQualifiedPackageName() {
        String packageName = psiPackage.getQualifiedName();
        if (packageName.length() > 0) {
             packageName += ".";
        }
        return packageName;
    }

    private String getXcordionTestCaseName() {
        return getConfiguration(moduleName).getXcordionBackingClassName();
    }
}

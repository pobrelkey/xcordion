package org.xcordion.ide.intellij.story;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.hiro.psi.PsiHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoryRunnerActionHandler extends EditorActionHandler {
    private static final int YES_RESPONSE = 0;
    private PsiHelper psiHelper;
    private boolean runInMemory;
    private Editor editor;

    public void execute(Editor editor, DataContext dataContext) {
        this.editor = editor;
        this.psiHelper = new PsiHelper(dataContext);

        determineRunningTestsInMemory();
        make();
    }

    private void determineRunningTestsInMemory() {
        runInMemory = YES_RESPONSE == Messages.showYesNoDialog("Wanna run test in memory?", "", Messages.getQuestionIcon());
    }

    private void make() {
        compileAndRunTests(getTestsToRun());
    }

    private List<TestToRun> getTestsToRun() {
        PsiFile storyPage = psiHelper.getCurrentFile();
        List<String> htmlFileNames = getConcordionTestFileNames(storyPage, editor);
        List<TestToRun> testFiles = new ArrayList<TestToRun>();

        for (String htmlFileName : htmlFileNames) {
            VirtualFile testHtmlFile = storyPage.getVirtualFile().getParent().findFileByRelativePath(htmlFileName);
            TestToRun testToRun = new TestToRun(htmlFileName);
            if (testHtmlFile != null) {
                setupTestToRun(testHtmlFile, testToRun);
            }
            testFiles.add(testToRun);
        }

        return testFiles;
    }

    private void compileAndRunTests(final List<TestToRun> testsToRun) {
        final Project project = psiHelper.getProject();
        final boolean autoShowErrorsInEditor = CompilerWorkspaceConfiguration.getInstance(project).AUTO_SHOW_ERRORS_IN_EDITOR;
        final boolean compileInBackground = CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND;
        CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND = false;

        Set<VirtualFile> testFiles = new HashSet<VirtualFile>();
        for (TestToRun testToRun : testsToRun) {
            if (testToRun.hasJavaFile()) {
                testFiles.add(testToRun.getHtmlVirtualFile());
                testFiles.add(testToRun.getJavaVirtualFile());
            }
        }

        CompileScope compileScope = CompilerManager.getInstance(project).createFilesCompileScope(testFiles.toArray(new VirtualFile[testFiles.size()]));

        CompilerManager.getInstance(project).compile(compileScope, new CompileStatusNotification() {
            public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                if (!aborted && errors == YES_RESPONSE) {
                    runTests(testsToRun, runInMemory);
                }
            }
        }, true);

        CompilerWorkspaceConfiguration.getInstance(project).AUTO_SHOW_ERRORS_IN_EDITOR = autoShowErrorsInEditor;
        CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND = compileInBackground;
    }

    private void runTests(final List<TestToRun> testsToRun, final boolean runInMemory) {

        PerformInBackgroundOption backgroundOption = new PerformInBackgroundOption() {
            public boolean shouldStartInBackground() {
                return false;
            }

            public void processSentToBackground() {
            }
        };

        ProgressManager.getInstance().run(new Task.Backgroundable(psiHelper.getProject(), "Story Runner", true, backgroundOption) {

            public void run(@NotNull ProgressIndicator progressIndicator) {
                JavaTestRunner testRunner = new JavaTestRunner(testsToRun, runInMemory);
                List<TestResultLogger> results = testRunner.getTestResults();

                PsiFile storyPage = psiHelper.getCurrentFile();
                new StoryPageResults(storyPage.getName(), storyPage.getText(), results).save();
            }
        });
    }

    private List<String> getConcordionTestFileNames(PsiFile storyPage, Editor editor) {
        List<String> testHtmlNames = new ArrayList<String>();

        String selectedContent;
        if(editor.getSelectionModel().hasSelection()) {
            selectedContent = editor.getSelectionModel().getSelectedText();
        } else {
            selectedContent = storyPage.getText();
        }

        Matcher matcher = Pattern.compile("<a.*?href=\"([../]+.*.html)\"").matcher(selectedContent);

        while (matcher.find()) {
            testHtmlNames.add(matcher.group(1));
        }
        return testHtmlNames;
    }

    private Module getModule(Project project, VirtualFile testJavaFile) {
        Module testModule = null;
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            if (moduleRootManager.getFileIndex().isInSourceContent(testJavaFile)) {
                testModule = module;
                break;
            }
        }
        return testModule;
    }

    private String toFullyQualifyJavaClassName(VirtualFile testJavaFile, PsiHelper psiHelper) {
        PsiFile psiFile = psiHelper.getPsiManager().findFile(testJavaFile);
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        return psiJavaFile.getPackageName() + '.' + testJavaFile.getNameWithoutExtension();
    }

    private void setupTestToRun(VirtualFile testHtmlFile, TestToRun testToRun) {
        VirtualFile testJavaFile = testHtmlFile.getParent().findFileByRelativePath(testHtmlFile.getNameWithoutExtension() + "Test.java");

        if (testJavaFile != null) {
            Module testModule = getModule(psiHelper.getProject(), testJavaFile);

            testToRun.setModule(testModule);
            testToRun.setHtmlVirtualFile(testHtmlFile);
            testToRun.setJavaVirtualFile(testJavaFile);
            testToRun.setFullyQualifiedClassName(toFullyQualifyJavaClassName(testJavaFile, psiHelper));
        }
    }
}
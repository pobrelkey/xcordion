package org.xcordion.ide.intellij.story;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.hiro.psi.PsiHelper;
import org.jetbrains.annotations.NotNull;
import static org.xcordion.ide.intellij.story.XcordionPsiFileHelper.isConcordionHtmlFile;
import static org.xcordion.ide.intellij.story.XcordionPsiFileHelper.isStoryPage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StoryRunnerActionHandler extends EditorActionHandler {
    private PsiHelper psiHelper;
    private TestRunnerStrategy strategy;
    private boolean runInMemory;
    private static final int YES_RESPONSE = 0;

    public void execute(Editor editor, DataContext dataContext) {
        this.psiHelper = new PsiHelper(dataContext);

        determineRunningTestsInMemory();
        determineTestRunnerStrategy(psiHelper.getCurrentFile());
        make(psiHelper.getProject());
    }

    private void determineRunningTestsInMemory() {
        runInMemory = YES_RESPONSE == Messages.showYesNoDialog("Wanna run test in memory?", "", Messages.getQuestionIcon());
    }

    private void determineTestRunnerStrategy(PsiFile currentFile) {
        if(isStoryPage(currentFile)) {
            strategy = TestRunnerStrategy.RUN_FROM_STORY_PAGE;
        } else if(isConcordionHtmlFile(currentFile)) {
            // not quite sure if this is the best way to run a test
            // since we probably wanna to see stuff on stdout as the test run
            // ... should something be printed out from TestResultLogger as messageLogged() is called?
            strategy = TestRunnerStrategy.RUN_FROM_TEST_PAGE;
        }
    }

    private void make(final Project project) {
        final List<TestToRun> testsToRun = strategy.getTestsToRun(psiHelper.getCurrentFile(), project, psiHelper);
        compileAndRunTests(testsToRun, project);
    }

    private void compileAndRunTests(final List<TestToRun> testsToRun, final Project project) {
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
                    runTests(testsToRun, project, psiHelper.getCurrentFile(), runInMemory);
                }
            }
        }, true);

        CompilerWorkspaceConfiguration.getInstance(project).AUTO_SHOW_ERRORS_IN_EDITOR = autoShowErrorsInEditor;
        CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND = compileInBackground;
    }

    private void runTests(final List<TestToRun> testsToRun, Project project, final PsiFile currentFile, final boolean runInMemory) {

        PerformInBackgroundOption backgroundOption = new PerformInBackgroundOption() {
            public boolean shouldStartInBackground() {
                return false;
            }

            public void processSentToBackground() {}
        };

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Story Runner", true, backgroundOption) {

            public void run(@NotNull ProgressIndicator progressIndicator) {
                JavaTestRunner testRunner = new JavaTestRunner(testsToRun, runInMemory);
                List<TestResultLogger> results = testRunner.getTestResults();

//                new JUnitResultsParser(results).printReport();
                strategy.processResult(currentFile, results);
            }
        });
    }
}
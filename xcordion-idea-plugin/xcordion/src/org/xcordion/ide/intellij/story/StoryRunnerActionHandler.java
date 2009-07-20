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
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.hiro.psi.PsiHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoryRunnerActionHandler extends EditorActionHandler {
    private PsiHelper psiHelper;
    public static final Pattern FULLY_QUALIFIED_NAME_PATTERN = Pattern.compile("[\\w-]+/[\\w-]+/[\\w-]+/[\\w-]+/(.*).html");
    public static final Pattern ACTIVE_DOC_FULLY_QUALIFIED_NAME_PATTERN = Pattern.compile("[\\w-]+/[\\w-]+/(.*).html");
    public static final Pattern CONCORDION_TEST_FILE_NAMES = Pattern.compile("<a.*?href=\"[../]+(.*.html)\"");
    private PsiFile storyPage;

    public void execute(Editor editor, DataContext dataContext) {
        this.psiHelper = new PsiHelper(dataContext);
        this.storyPage = getStoryPage();

        make(psiHelper.getProject());
    }


    public void make(final Project project) {
        final List<TestToRun> testsToRun = parseStoryPageForTests(project);
        compileModulesAndRunTests(testsToRun, project);
    }

    private void compileModulesAndRunTests(final List<TestToRun> testsToRun, Project project) {
        final boolean autoShowErrorsInEditor = CompilerWorkspaceConfiguration.getInstance(project).AUTO_SHOW_ERRORS_IN_EDITOR;
        final boolean compileInBackground = CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND;
        CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND = false;

        Set<VirtualFile> testFiles = new HashSet<VirtualFile>();
        for (TestToRun testToRun : testsToRun) {
            if (testToRun.hasJavaFile()) {
                testFiles.add(testToRun.getJavaFile());
            }
        }

        CompileScope compileScope = CompilerManager.getInstance(project).createFilesCompileScope(testFiles.toArray(new VirtualFile[testFiles.size()]));

        CompilerManager.getInstance(project).compile(compileScope, new CompileStatusNotification() {
            public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                if (!aborted && errors == 0) {
                    runTests(testsToRun);
                }
            }
        }, true);

        CompilerWorkspaceConfiguration.getInstance(project).AUTO_SHOW_ERRORS_IN_EDITOR = autoShowErrorsInEditor;
        CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND = compileInBackground;
    }

    private void runTests(final List<TestToRun> testsToRun) {
        Runnable runnable = new Runnable() {
            public void run() {
                JavaTestRunner testRunner = new JavaTestRunner(testsToRun);
                List<TestResultLogger> results = testRunner.getTestResults();
                new JUnitResultsParser(results).printReport();
                new StoryPageResults(storyPage.getName(), storyPage.getText(), results).save();
            }
        };
        ProgressManager.getInstance().runProcessWithProgressSynchronously(runnable, "Story Runner", true, null);
    }

    private List<TestToRun> parseStoryPageForTests(Project project) {
        List<String> htmlFileNames = getConcordionTestFileNames(storyPage);
        List<TestToRun> testFiles = new ArrayList<TestToRun>();

        for (String htmlFileName : htmlFileNames) {
            VirtualFile testHtmlFile = storyPage.getVirtualFile().getParent().findFileByRelativePath(htmlFileName);
            TestToRun testToRun = new TestToRun(htmlFileName);
            if (testHtmlFile != null) {
                VirtualFile testJavaFile = testHtmlFile.getParent().findFileByRelativePath(testHtmlFile.getNameWithoutExtension() + "Test.java");

                if (testJavaFile != null) {
                    Module[] modules = ModuleManager.getInstance(project).getModules();
                    for (Module module : modules) {
                        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
                        if (moduleRootManager.getFileIndex().isInSourceContent(testJavaFile)) {
                            testToRun.setModule(module);
                            break;
                        }
                    }

                    testToRun.setJavaVirtualFile(testJavaFile);
                    testToRun.setFullyQualifiedClassName(toFullyQualifyJavaClassName(testJavaFile));
                }
            }
            testFiles.add(testToRun);
        }

        return testFiles;
    }

    private String toFullyQualifyJavaClassName(VirtualFile testJavaFile) {
        PsiFile psiFile = psiHelper.getPsiManager().findFile(testJavaFile);
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        return psiJavaFile.getPackageName() + '.' + testJavaFile.getNameWithoutExtension();
    }

    private PsiFile getStoryPage() {
        return psiHelper.getCurrentFile();
    }

    private List<String> getConcordionTestFileNames(PsiFile storyPage) {
        List<String> testHtmlNames = new ArrayList<String>();
        Matcher matcher = Pattern.compile("<a.*?href=\"([../]+.*.html)\"").matcher(storyPage.getText());

        while (matcher.find()) {
            testHtmlNames.add(matcher.group(1));
        }
        return testHtmlNames;
    }
}

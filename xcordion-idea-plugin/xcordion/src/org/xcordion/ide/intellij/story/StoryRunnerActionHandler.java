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
import com.intellij.psi.PsiFile;
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
        final List<TestToRun> testsToRun = parseStoryPageForTestClassNames();
        compileModulesAndRunTests(testsToRun, project);
    }

    private void compileModulesAndRunTests(final List<TestToRun> testsToRun, Project project) {
        Module[] modulesToCompile = modulesToCompile(testsToRun);

        CompilerWorkspaceConfiguration workspaceConfiguration = CompilerWorkspaceConfiguration.getInstance(project);
        final boolean autoShowErrorsInEditor = workspaceConfiguration.AUTO_SHOW_ERRORS_IN_EDITOR;
        final boolean compileInBackground = workspaceConfiguration.COMPILE_IN_BACKGROUND;
        workspaceConfiguration.COMPILE_IN_BACKGROUND = false;

        CompilerManager compilerManager = CompilerManager.getInstance(project);
        CompileScope compileScope = compilerManager.createModuleGroupCompileScope(project, modulesToCompile, true);

        compilerManager.compile(compileScope, new CompileStatusNotification() {
            public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                if (!aborted && errors == 0) {
                    runTests(testsToRun);
                }
            }
        }, true);

        workspaceConfiguration.AUTO_SHOW_ERRORS_IN_EDITOR = autoShowErrorsInEditor;
        workspaceConfiguration.COMPILE_IN_BACKGROUND = compileInBackground;
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

    private Module[] modulesToCompile(List<TestToRun> testsToRun) {
        Set<Module> modulesToCompile = new HashSet<Module>();
        for (TestToRun test : testsToRun) {
            modulesToCompile.add(test.getModule());
        }
        return modulesToCompile.toArray(new Module[modulesToCompile.size()]);
    }

    private List<TestToRun> parseStoryPageForTestClassNames() {
        List<String> htmlFileNames = getConcordionTestFileNames(storyPage);
        List<TestToRun> tests = new ArrayList<TestToRun>();

        for (String htmlFileName : htmlFileNames) {
            String moduleName = stripModuleName(htmlFileName);
            Module module = getModule(moduleName);

            if (isNetstreamProject()) {
                if ("active-documentation".equals(moduleName)) {
                    tests.add(new TestToRun(toFullyQualifiedTestName(htmlFileName, ACTIVE_DOC_FULLY_QUALIFIED_NAME_PATTERN), module));
                } else {
                    tests.add(new TestToRun(toFullyQualifiedTestName(htmlFileName, FULLY_QUALIFIED_NAME_PATTERN), module));
                }
            } else {
                tests.add(new TestToRun(toFullyQualifiedTestName(htmlFileName, ACTIVE_DOC_FULLY_QUALIFIED_NAME_PATTERN), module));
            }
        }
        return tests;
    }

    private boolean isNetstreamProject() {
        return "netstream".equals(psiHelper.getProject().getName());
    }

    private String toFullyQualifiedTestName(String htmlFileName, Pattern fullyQualifiedNamePattern) {
        Matcher matcher = fullyQualifiedNamePattern.matcher(htmlFileName);
        String result = "";
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result.replace("/", ".") + "Test";
    }

    private String stripModuleName(String htmlFileName) {
        return htmlFileName.split("/")[0];
    }

    private PsiFile getStoryPage() {
        return psiHelper.getCurrentFile();
    }

    private Module getModule(String moduleName) {
        return ModuleManager.getInstance(psiHelper.getProject()).findModuleByName(moduleName);
    }

    private List<String> getConcordionTestFileNames(PsiFile storyPage) {
        List<String> testHtmlNames = new ArrayList<String>();
        Matcher matcher = CONCORDION_TEST_FILE_NAMES.matcher(storyPage.getText());

        while (matcher.find()) {
            testHtmlNames.add(matcher.group(1));
        }
        return testHtmlNames;
    }
}

package org.xcordion.ide.intellij.story;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.compiler.CompileContext;
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
        if (!compileModules(testsToRun, project)) {
            return;
        }

        Runnable runnable = new Runnable() {
            public void run() {

                runTests(testsToRun);
            }
        };

        ProgressManager.getInstance().runProcessWithProgressSynchronously(runnable, "Story Runner", true, null);
    }

    private void runTests(List<TestToRun> testsToRun) {

        JavaTestRunner testRunner = new JavaTestRunner(testsToRun);
        List<TestResultLogger> results = testRunner.getTestResults();
        new JUnitResultsParser(results).printReport();
        new StoryPageResults(storyPage.getName(), storyPage.getText(), results).save();
    }

    private boolean compileModules(List<TestToRun> testsToRun, Project project) {
        Set<Module> modulesToCompile = new HashSet<Module>();

        final boolean[] success = new boolean[]{true};

        for (TestToRun test : testsToRun) {
            modulesToCompile.add(test.getModule());
        }

        final boolean autoShowErrorsInEditor = CompilerWorkspaceConfiguration.getInstance(project).AUTO_SHOW_ERRORS_IN_EDITOR;
        final boolean compileInBackground = CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND;
        CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND = true;

        for (Module module : modulesToCompile) {
            CompilerManager.getInstance(project).make(module, new CompileStatusNotification() {
                public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                    if (aborted || errors > 0) {
                        success[0] = false;
                    }
                }
            });
            if (!success[0]) {
                break;
            }
        }
        CompilerWorkspaceConfiguration.getInstance(project).AUTO_SHOW_ERRORS_IN_EDITOR = autoShowErrorsInEditor;
        CompilerWorkspaceConfiguration.getInstance(project).COMPILE_IN_BACKGROUND = compileInBackground;

        return success[0];
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

    public class TestToRun {
        private String name;
        private Module module;

        TestToRun(String name, Module module) {
            this.name = name;
            this.module = module;
        }

        public String getName() {
            return name;
        }

        public Module getModule() {
            return module;
        }
    }
}

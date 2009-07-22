package org.xcordion.ide.intellij.story;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.hiro.psi.PsiHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum TestRunnerStrategy {
    RUN_FROM_STORY_PAGE() {
        public List<TestToRun> getTestsToRun(PsiFile storyPage, Project project, PsiHelper psiHelper) {
            List<String> htmlFileNames = getConcordionTestFileNames(storyPage);
            List<TestToRun> testFiles = new ArrayList<TestToRun>();

            for (String htmlFileName : htmlFileNames) {
                VirtualFile testHtmlFile = storyPage.getVirtualFile().getParent().findFileByRelativePath(htmlFileName);
                TestToRun testToRun = new TestToRun(htmlFileName);
                if (testHtmlFile != null) {
                    setupTestToRun(project, psiHelper, testHtmlFile, testToRun);
                }
                testFiles.add(testToRun);
            }

            return testFiles;
        }

        public void processResult(PsiFile currentPage, List<TestResultLogger> results) {
            new StoryPageResults(currentPage.getName(), currentPage.getText(), results).save();
        }

        private List<String> getConcordionTestFileNames(PsiFile storyPage) {
            List<String> testHtmlNames = new ArrayList<String>();
            Matcher matcher = Pattern.compile("<a.*?href=\"([../]+.*.html)\"").matcher(storyPage.getText());

            while (matcher.find()) {
                testHtmlNames.add(matcher.group(1));
            }
            return testHtmlNames;
        }
    },
    RUN_FROM_TEST_PAGE() {
        List<TestToRun> getTestsToRun(PsiFile currentPage, Project project, PsiHelper psiHelper) {
            VirtualFile testHtmlFile = currentPage.getVirtualFile();
            TestToRun testToRun = new TestToRun(testHtmlFile.getName());

            setupTestToRun(project, psiHelper, testHtmlFile, testToRun);
            return Collections.singletonList(testToRun);
        }

        public void processResult(PsiFile currentPage, List<TestResultLogger> results) {
            if (!results.isEmpty()) {
                File file = new File(results.get(0).getTestOutputPath());
                if(file != null) {
                    BrowserUtil.launchBrowser(file.getAbsolutePath());
                }
            }
        }
    };

    private static void setupTestToRun(Project project, PsiHelper psiHelper, VirtualFile testHtmlFile, TestToRun testToRun) {
        VirtualFile testJavaFile = testHtmlFile.getParent().findFileByRelativePath(testHtmlFile.getNameWithoutExtension() + "Test.java");

        if (testJavaFile != null) {
            Module testModule = getModule(project, testJavaFile);

            testToRun.setModule(testModule);
            testToRun.setHtmlVirtualFile(testHtmlFile);
            testToRun.setJavaVirtualFile(testJavaFile);
            testToRun.setFullyQualifiedClassName(toFullyQualifyJavaClassName(testJavaFile, psiHelper));
        }
    }

    abstract List<TestToRun> getTestsToRun(PsiFile currentPage, Project project, PsiHelper psiHelper);

    abstract void processResult(PsiFile currentPage, List<TestResultLogger> results);

    private static Module getModule(Project project, VirtualFile testJavaFile) {
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

    private static String toFullyQualifyJavaClassName(VirtualFile testJavaFile, PsiHelper psiHelper) {
        PsiFile psiFile = psiHelper.getPsiManager().findFile(testJavaFile);
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        return psiJavaFile.getPackageName() + '.' + testJavaFile.getNameWithoutExtension();
    }
}

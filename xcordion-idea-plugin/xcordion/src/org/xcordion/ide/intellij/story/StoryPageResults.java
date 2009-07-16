package org.xcordion.ide.intellij.story;

import com.intellij.openapi.ui.Messages;
import static com.intellij.openapi.ui.Messages.showMessageDialog;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoryPageResults {
    private final String fileName;
    private final String originalStoryPageText;
    private final List<JunitBuildLogger> results;
    private static final String START_PATTERN = "href=\"([../]*([\\w-]+/){2,9}";
    private static final String END_PATTERN = ")\"";
    private static String javaTmpDirectory = System.getProperty("java.io.tmpdir");
    private String finalText;

    public StoryPageResults(String fileName, String originalStoryPageText, List<JunitBuildLogger> results) {
        this.fileName = fileName;
        this.originalStoryPageText = originalStoryPageText;
        this.results = results;
    }

    public static String getJavaTmpDirectory() {
        return javaTmpDirectory;
    }

    public void save() {
        finalText = originalStoryPageText;
        for (JunitBuildLogger result : results) {
            Pattern pattern = Pattern.compile(START_PATTERN + getTestHtmlPath(result) + END_PATTERN);
            Matcher matcher = pattern.matcher(finalText);

            if (matcher.find()) {
                this.finalText = matcher.replaceAll("href=\""+result.getTestOutputPath()+"\"");
            }
        }

        File baseOutputDirectory = new File(getJavaTmpDirectory(), "concordion");
        File outputFile = new File(baseOutputDirectory, fileName);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outputFile);
            fileOutputStream.write(finalText.getBytes());
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fileOutputStream);
        }

        System.out.println("Test results page:");
        System.out.println(outputFile.getAbsolutePath());

        showMessageDialog("Test results page: " + outputFile.getAbsolutePath(), "Test Results", Messages.getInformationIcon());
    }

    public String getFinalText() {
        return finalText;
    }

    private void close(OutputStream fileOutputStream) {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTestHtmlPath(JunitBuildLogger result) {
        String fqClassName = result.getTestClass().getName();
        return fqClassName.replaceAll("\\.", "/").substring(0, fqClassName.length() - 4) + ".html";
    }
}

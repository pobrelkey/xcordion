package org.xcordion.ide.intellij.story;

import com.intellij.ide.BrowserUtil;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoryPageResults {
    private final String fileName;
    private final String originalStoryPageText;
    private final List<TestResultLogger> results;
    private static String javaTmpDirectory = System.getProperty("java.io.tmpdir");
    private String finalText;

    public StoryPageResults(String fileName, String originalStoryPageText, List<TestResultLogger> results) {
        this.fileName = fileName;
        this.originalStoryPageText = originalStoryPageText;
        this.results = results;
    }

    public static String getJavaTmpDirectory() {
        return javaTmpDirectory;
    }

    public void save() {
        finalText = originalStoryPageText;
        for (TestResultLogger result : results) {


            String pattern = "(<a\\s[^>]*\\bhref=\")(" + Pattern.quote(getTestHtmlPath(result)) + ")(\"[^>]*>.*?</a>)";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(finalText);

            StringBuffer buf = new StringBuffer();
            while (matcher.find()) {
                String path = result.getTestOutputPath();
                if (result.outcome() == TestResultLogger.TestOutcome.NOT_FOUND) {
                    // TODO: point to fully-qualified path to original HTML file, if any
                    path = matcher.group(2);
                }
                matcher.appendReplacement(buf, matcher.group(1) + path + matcher.group(3) + " <span " + result.outcome().htmlStyle() + ">" + result.outcome().text() + "</span>");
            }
            matcher.appendTail(buf);
            this.finalText = buf.toString();
        }

        File outputFile = writeFile();

        System.out.println("Test results page:");
        System.out.println(outputFile.getAbsolutePath());

        BrowserUtil.launchBrowser(outputFile.getAbsolutePath());
    }

    private File writeFile() {
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
        return outputFile;
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

    private String getTestHtmlPath(TestResultLogger result) {
        return result.getTestName();
//        return fqClassName.replaceAll("\\.", "/").substring(0, fqClassName.length() - 4) + ".html";
    }
}

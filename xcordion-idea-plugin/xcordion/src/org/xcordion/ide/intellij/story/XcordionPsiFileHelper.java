package org.xcordion.ide.intellij.story;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import org.xcordion.ide.intellij.XcordionNamespace;

import java.util.regex.Pattern;

public class XcordionPsiFileHelper {

    public static final Pattern STORY_PAGE_STYLESHEET_PATTERN = Pattern.compile("<link .*[href=\"].*(story_overview.css\" />)");

    public static boolean isConcordionHtmlFile(PsiFile psiFile) {
        String fileContents = psiFile.getText();
        return psiFile instanceof HtmlFileImpl &&
                (fileContents.contains(XcordionNamespace.NAMESPACE_XCORDION.getNamespace())
                        || fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_2007.getNamespace())
                        || fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_ANCIENT.getNamespace())
                        || fileContents.contains(XcordionNamespace.NAMESPACE_CONCORDION_OLD.getNamespace()));
    }

    public static boolean isStoryPage(PsiFile psiFile) {
        if (psiFile instanceof XmlFileImpl) {
            String fileContents = psiFile.getText();
            if (STORY_PAGE_STYLESHEET_PATTERN.matcher(fileContents).find()) {
                return true;
            }
        }
        return false;
    }
}

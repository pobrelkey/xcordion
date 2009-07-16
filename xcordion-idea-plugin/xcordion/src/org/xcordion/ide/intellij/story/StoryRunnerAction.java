package org.xcordion.ide.intellij.story;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import org.hiro.psi.PsiHelper;

import java.util.regex.Pattern;

public class StoryRunnerAction extends EditorAction {
    public static final Pattern STYLESHEET_PATTERN = Pattern.compile("<link .*[href=\"].*(story_overview.css\" />)");

    protected StoryRunnerAction() {
        super(new StoryRunnerActionHandler());
    }

    @Override
    public void update(Editor editor, Presentation presentation, DataContext dataContext) {
        PsiHelper psiHelper = new PsiHelper(dataContext);

        boolean enabled = false;
        PsiFile psiFile = psiHelper.getCurrentFile();

        if (psiFile instanceof XmlFileImpl) {
            String fileContents = psiFile.getText();
            if (STYLESHEET_PATTERN.matcher(fileContents).find()) {
                enabled = true;
            }
        }

        presentation.setEnabled(enabled);
    }
}

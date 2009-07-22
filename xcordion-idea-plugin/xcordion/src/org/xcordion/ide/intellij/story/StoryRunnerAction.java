package org.xcordion.ide.intellij.story;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.psi.PsiFile;
import org.hiro.psi.PsiHelper;

import java.util.regex.Pattern;

public class StoryRunnerAction extends EditorAction {
    public static final Pattern STORY_PAGE_STYLESHEET_PATTERN = Pattern.compile("<link .*[href=\"].*(story_overview.css\" />)");

    protected StoryRunnerAction() {
        super(new StoryRunnerActionHandler());
    }

    @Override
    public void update(Editor editor, Presentation presentation, DataContext dataContext) {
        PsiHelper psiHelper = new PsiHelper(dataContext);
        PsiFile psiFile = psiHelper.getCurrentFile();
        
        if(XcordionPsiFileHelper.isStoryPage(psiFile) || XcordionPsiFileHelper.isConcordionHtmlFile(psiFile)) {
            presentation.setEnabled(true);
        } else {
            presentation.setEnabled(false);
        }
    }
}

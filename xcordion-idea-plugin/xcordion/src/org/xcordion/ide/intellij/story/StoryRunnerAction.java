package org.xcordion.ide.intellij.story;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;

public class StoryRunnerAction extends EditorAction {
    protected StoryRunnerAction() {
        super(new StoryRunnerActionHandler());
    }

    @Override
    public void update(Editor editor, Presentation presentation, DataContext dataContext) {
        presentation.setEnabled(true);
    }
}

package org.xcordion.ide.intellij.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;

public class ToggleTestHtmlJavaAction extends EditorAction {

    protected ToggleTestHtmlJavaAction() {
        super(new ToggleTestHtmlJavaActionHandler());
    }

    @Override
    public void update(Editor editor, Presentation presentation, DataContext dataContext) {
        presentation.setEnabled(true);
    }
}

package org.xcordion.ide.intellij.story;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.module.Module;
import jedi.functional.Coercions;
import org.hiro.psi.PsiHelper;

import java.util.List;

public class StoryRunnerActionHandler extends EditorActionHandler {
    private PsiHelper psiHelper;
    private DataContext dataContext;

    public void execute(Editor editor, DataContext dataContext) {
        this.dataContext = dataContext;
        this.psiHelper = new PsiHelper(dataContext);
        Module module = psiHelper.getModule(dataContext);

        new JavaTestRunner(module, getTestClassNames());
    }

    private List<String> getTestClassNames() {
        return Coercions.list("org.test.FirstTest", "org.test.SecondTest");
    }
}

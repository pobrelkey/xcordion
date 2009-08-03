package org.xcordion.ide.intellij.story;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class RunInMemorySelector implements ApplicationComponent {
    private static int YES = 0;

    public RunInMemorySelector() {
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "RunInMemorySelector";
    }

    public boolean runInMemory() {
        return YES == Messages.showYesNoDialog("Wanna run test in memory?", "", Messages.getQuestionIcon());        
    }
}

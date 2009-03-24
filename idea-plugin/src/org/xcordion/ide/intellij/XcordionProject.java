package org.xcordion.ide.intellij;

import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class XcordionProject implements ProjectComponent {
    public XcordionProject() {
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    @NonNls
    public String getComponentName() {
        return "XcordionProject";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }
}

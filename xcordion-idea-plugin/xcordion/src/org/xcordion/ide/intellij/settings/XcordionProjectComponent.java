package org.xcordion.ide.intellij.settings;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import static org.apache.commons.lang.StringUtils.capitalize;
import static org.xcordion.ide.intellij.settings.AbstractXcordionComponent.COMPONENT_NAME;
import static org.xcordion.ide.intellij.settings.XcordionSettingsContext.setProjectSettings;

@State(
        name = COMPONENT_NAME,
        storages = {
                @Storage(id = COMPONENT_NAME, file = "$PROJECT_FILE$")
        }
)
public class XcordionProjectComponent extends AbstractXcordionComponent implements ProjectComponent {
    private final Project project;

    public XcordionProjectComponent(Project project) {
        this.project = project;
        XcordionSettings settings = new XcordionSettings();
        settings.setShowConfirmationMessage(true);
        settings.setXcordionBackingClassName(getClassNamePrefix(project) + "ConcordionTestCase");
        setSettings(settings);
    }

    private String getClassNamePrefix(Project project) {
        return project.isDefault() ? "" : capitalize(project.getName());
    }

    public void projectOpened() {
        saveSettings();
    }

    protected void saveSettings() {
        if (getSettings()!=null) {
            setProjectSettings(project, getSettings());
        }
    }
}
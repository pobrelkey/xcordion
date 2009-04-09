package org.xcordion.ide.intellij.settings;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import static org.xcordion.ide.intellij.settings.XcordionProjectComponent.COMPONENT_NAME;
import static org.xcordion.ide.intellij.settings.XcordionSettingsContext.getProjectSettings;
import static org.xcordion.ide.intellij.settings.XcordionSettingsContext.setModuleSettings;

@State(
        name = COMPONENT_NAME,
        storages = {
                @Storage(id = COMPONENT_NAME, file = "$MODULE_FILE$")
        }
)
public class XcordionModuleComponent extends AbstractXcordionComponent implements ModuleComponent {
    private final Module module;

    public XcordionModuleComponent(Module module) {
        this.module = module;
    }

    public void moduleAdded() {
        saveSettings();
    }

    protected void saveSettings() {
        if (getSettings() != null) {
            setModuleSettings(module, getSettings());
        }
    }

    protected XcordionSettings loadSettings() {
        if (getSettings() == null) {
            setSettings(getProjectSettings(module.getProject()));
        }
        return getSettings();
    }
}
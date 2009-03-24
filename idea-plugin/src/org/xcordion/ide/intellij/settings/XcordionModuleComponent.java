package org.xcordion.ide.intellij.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import static org.xcordion.ide.intellij.settings.XcordionSettingsContext.setConfiguration;

import javax.swing.*;

@State(
        name = XcordionModuleComponent.COMPONENT_NAME,
        storages = {
                @Storage(id = XcordionModuleComponent.COMPONENT_NAME, file = "$MODULE_FILE$")
        }
)
public class XcordionModuleComponent implements ModuleComponent, Configurable, PersistentStateComponent<XcordionSettings> {
    public static final String COMPONENT_NAME = "XcordionConfiguration";

    private final Module module;
    private XcordionSettings settings = new XcordionSettings();
    private SettingsPanel settingsPanel;

    public XcordionModuleComponent(Module module) {
        this.module = module;
        this.settings.setXcordionBackingClassName(module.getName() + "ConcordionTestCase");
        this.settings.setShowConfirmationMessage(true);
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    public void moduleAdded() {
        setConfiguration(module.getName(), getState());
    }

    @NotNull
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @Nls
    public String getDisplayName() {
        return "Xcordion";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return "";
    }

    public JComponent createComponent() {
        settingsPanel = new SettingsPanel(settings);
        reset();
        return settingsPanel;
    }

    public boolean isModified() {
        return !settings.equals(settingsPanel.getSettings());
    }

    public void apply() throws ConfigurationException {
        settings = settingsPanel.getSettings();
        setConfiguration(module.getName(), settings);
    }

    public void reset() {
        settingsPanel.setSettings(settings);
    }

    public void disposeUIResources() {
        settingsPanel = null;
    }

    public XcordionSettings getState() {
        return settings;
    }

    public void loadState(XcordionSettings state) {
        XmlSerializerUtil.copyBean(state, settings);
    }
}

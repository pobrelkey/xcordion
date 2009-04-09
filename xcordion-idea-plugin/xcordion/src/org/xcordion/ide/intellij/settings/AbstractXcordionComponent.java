package org.xcordion.ide.intellij.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import static com.intellij.util.xmlb.XmlSerializerUtil.copyBean;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class AbstractXcordionComponent implements ProjectComponent, Configurable, PersistentStateComponent<XcordionSettings> {
    static final String COMPONENT_NAME = "XcordionConfiguration";

    private XcordionSettings settings;
    private SettingsPanel settingsPanel;

    abstract void saveSettings();

    XcordionSettings loadSettings() {
        return getSettings();
    }

    public void projectOpened() {
    }

    public void projectClosed() {
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
        saveSettings();
    }

    public void reset() {
        settingsPanel.setSettings(getState());
    }

    public void disposeUIResources() {
        settingsPanel = null;
    }

    public XcordionSettings getState() {
        return loadSettings();
    }

    public void loadState(XcordionSettings state) {
        if (settings==null) {
            settings = new XcordionSettings();
        }
        copyBean(state, settings);
    }

    XcordionSettings getSettings() {
        return settings;
    }

    void setSettings(XcordionSettings settings) {
        this.settings = settings;
    }
}
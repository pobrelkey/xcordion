package org.xcordion.ide.intellij.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
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

    private XcordionSettings settings = new XcordionSettings();
    private SettingsPanel settingsPanel;
    private Project project;
    private Module module;

    public XcordionModuleComponent(Project project, Module module) {
        this.project = project;
        this.module = module;
        settings.setXcordionBackingClassName(module.getName() + "ConcordionTestCase");
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
//        return IconHelper.getIcon(IconHelper.XCORDION_ICON);
        return null;
    }

    public String getHelpTopic() {
        return "";
    }

    public JComponent createComponent() {
        settingsPanel = new SettingsPanel();
        reset();
        return settingsPanel;
    }

    public boolean isModified() {
        return !settings.getXcordionBackingClassName().equals(settingsPanel.getXcordionBackingClassName());
    }

    public void apply() throws ConfigurationException {
        settings.setXcordionBackingClassName(settingsPanel.getXcordionBackingClassName());
        setConfiguration(module.getName(), settings);
    }

    public void reset() {
        settingsPanel.setXcordionBackingClassName(settings.getXcordionBackingClassName());
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

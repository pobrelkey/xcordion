package org.xcordion.ide.intellij.settings;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

public class XcordionSettingsContext {
    private static final Map<String, XcordionSettings> PROJECT_SETTINGS = new HashMap<String, XcordionSettings>();
    private static final Map<String, XcordionSettings> MODULE_SETTINGS = new HashMap<String, XcordionSettings>();

    public static XcordionSettings getModuleSettings(Module module) {
        XcordionSettings settings = MODULE_SETTINGS.get(getModuleKey(module));
        if (settings==null || settings.getXcordionBackingClassName()==null) {
            settings = PROJECT_SETTINGS.get(module.getProject().getName());
        }                                                                       
        return settings;
    }

    public static void setModuleSettings(Module module, XcordionSettings settings) {
        MODULE_SETTINGS.put(getModuleKey(module), settings);
    }

    private static String getModuleKey(Module module) {
        return module.getProject().getName() + "$" + module.getName();
    }

    public static XcordionSettings getProjectSettings(Project project) {
        return PROJECT_SETTINGS.get(project.getName());
    }

    public static void setProjectSettings(Project project, XcordionSettings settings) {
        PROJECT_SETTINGS.put(project.getName(), settings);
    }
}

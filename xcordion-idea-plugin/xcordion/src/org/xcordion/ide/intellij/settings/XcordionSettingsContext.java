package org.xcordion.ide.intellij.settings;

import java.util.Map;
import java.util.HashMap;

public class XcordionSettingsContext {
    private static final Map<String, XcordionSettings> MODULE_SETTINGS = new HashMap<String, XcordionSettings>();

    public static XcordionSettings getConfiguration(String moduleName) {
        XcordionSettings settings = MODULE_SETTINGS.get(moduleName);
        if (settings==null) {
            settings = new XcordionSettings();
            MODULE_SETTINGS.put(moduleName, settings);
        }
        return settings;
    }

    public static void setConfiguration(String moduleName, XcordionSettings configuration) {
        MODULE_SETTINGS.put(moduleName, configuration);
    }
}

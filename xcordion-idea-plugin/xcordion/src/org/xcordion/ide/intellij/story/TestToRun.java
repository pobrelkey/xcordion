package org.xcordion.ide.intellij.story;

import com.intellij.openapi.module.Module;

public class TestToRun {
    private String name;
    private Module module;

    TestToRun(String name, Module module) {
        this.name = name;
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public Module getModule() {
        return module;
    }
}

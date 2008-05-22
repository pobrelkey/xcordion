package org.xcordion.ide.intellij;
/*
See http://intellij.net/forums/thread.jspa?messageID=5157247&#5157247
 */

import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NonNls;

public class XcordionProject implements ProjectComponent {
    private ReferenceProvidersRegistry registry;

    public XcordionProject(ReferenceProvidersRegistry registry) {
        this.registry = registry;
    }

    public void projectOpened() {

    }


    public void projectClosed() {
    }

    @NonNls
    public String getComponentName() {
        return "XcordionProject";
    }

    public void initComponent() {
        CompletionUtil.registerCompletionData(StdFileTypes.HTML, new XcordionHtmlCompletionData());
    }

    public void disposeComponent() {
    }

}
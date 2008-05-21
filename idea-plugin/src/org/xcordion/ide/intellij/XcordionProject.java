package org.xcordion.ide.intellij;
/*
See http://intellij.net/forums/thread.jspa?messageID=5157247&#5157247
 */

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.filters.TrueFilter;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.PsiManager;
import com.intellij.codeInsight.completion.CompletionUtil;
import org.jetbrains.annotations.NonNls;
import org.xcordion.ide.intellij.XcordionHtmlCompletionData;

public class XcordionProject implements ProjectComponent {
    private ReferenceProvidersRegistry registry;

    public XcordionProject(ReferenceProvidersRegistry registry) {
        this.registry = registry;
//        //registry.registerReferenceProvider(XmlTag.class, new XcordionReferenceProvider(psiManager));
    }

    public void projectOpened() {
        registry.registerXmlAttributeValueReferenceProvider(
                null,
                TrueFilter.INSTANCE,
                new XcordionReferenceProvider());
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
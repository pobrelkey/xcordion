package xcordion.lang.java;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.jdom.Document;
import org.jdom.JDOMException;
import xcordion.api.EvaluationContext;
import xcordion.api.IgnoreState;
import xcordion.api.ResourceReference;
import xcordion.api.TestElement;
import xcordion.impl.DefaultCommandRepository;
import xcordion.impl.XcordionImpl;
import xcordion.impl.events.ResultSummariser;
import xcordion.impl.events.XcordionEventsBroadcaster;
import xcordion.impl.theme.ConcordionMarkupTheme;
import xcordion.util.DocumentFactory;
import xcordion.util.ResourceFinder;
import xcordion.util.WrappingIterable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SimpleXcordionRunner {
    private static final String PROPERTY_OUTPUT_DIR = "xcordion.outputPath";

    private Object testInstance;
    private String resourceName;
    private String outputPath;
    private JDomTestDocument testDocument;
    private ValueTranslator valueTranslator;

    public SimpleXcordionRunner(Object testInstance) {
        this.testInstance = testInstance;
    }

    public File getOutputDir() {
        String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
        if (outputPath == null) {
            return new File(System.getProperty("java.io.tmpdir"), "xcordion");
        }
        return new File(outputPath);
    }

    public void runTest() throws IOException, JDOMException {
        runTest(true);
    }

    public void runTest(boolean expectedToPass) throws IOException, JDOMException {
        Class testClass = testInstance.getClass();
        XcordionEventsBroadcaster broadcaster = new XcordionEventsBroadcaster();
        JDomTestDocument testDocument = getTestDocument();
        ConcordionMarkupTheme theme = new ConcordionMarkupTheme();
        List<ResourceReference<JDomTestDocument.JDomTestElement>> resourceReferences = theme.getResourceReferences();

        ResultSummariser summary = new ResultSummariser();
        summary.setExpectedToPass(expectedToPass);
        broadcaster.addListener(summary);

        broadcaster.addListener(theme);

        XcordionImpl xcordion = new XcordionImpl(new DefaultCommandRepository(), broadcaster);

        Ognl.OgnlEvaluationContext ognlEvaluationContext = new Ognl().newContext("ognl", testInstance);
        ValueTranslatingEvaluationContext<Ognl.OgnlEvaluationContext> translatingContext = new ValueTranslatingEvaluationContext(ognlEvaluationContext);
        xcordion.run(testDocument, translatingContext);

        String outputFilePath = new MarkupWriter(getOutputDir()).write(testDocument, getOutputPath(), testClass, resourceReferences);

        System.out.println("Xcordion result for " + testClass.getSimpleName() + ": " + summary.getScoreLine());
        String message = summary.getMessage();
        if (!summary.isHappy()) {
            message += " - see output for details: " + outputFilePath;
        } else {
            System.out.println(outputFilePath);
        }
        Assert.assertTrue(message, summary.isSuccessful());
    }

    public String getOutputPath() {
        if (outputPath == null) {
            String resourceName = getResourceName();
            outputPath = testInstance.getClass().getName().replace('.', File.separatorChar);
            if (outputPath.indexOf(File.separatorChar) != -1) {
                outputPath = outputPath.substring(0, outputPath.lastIndexOf(File.separatorChar)) + File.separatorChar + resourceName;
            } else {
                outputPath = resourceName;
            }
        }
        return outputPath;
    }

    public JDomTestDocument getTestDocument() throws IOException, JDOMException {
        if (testDocument == null) {
            Class testClass = testInstance.getClass();
            String resourceName = getResourceName();
            InputStream inputStream = null;
            Document doc;
            try {
                inputStream = new ResourceFinder(testClass).getResourceAsURL(resourceName).openStream();
                doc = DocumentFactory.document(inputStream);
            } finally {
                if (inputStream != null) {
                    try { inputStream.close(); } catch (IOException ignored) {}
                }
            }
            testDocument = new JDomTestDocument(doc);
        }
        return testDocument;
    }

    public String getResourceName() {
        if (this.resourceName == null) {
            Class testClass = testInstance.getClass();
            ResourceFinder finder = new ResourceFinder(testClass);
            List<String> resourceNames = new ArrayList<String>();

            String simpleName = testClass.getSimpleName();
            permuteName(resourceNames, simpleName);
            if (simpleName.endsWith("Test")) {
                permuteName(resourceNames, simpleName.substring(0, simpleName.length() - 4));
            }

            for (String resourceName : resourceNames) {
                if (tryResourceName(finder, resourceName)) {
                    this.resourceName = resourceName;
                    return resourceName;
                }
            }
            throw new AssertionFailedError("Can't find any test resources related to this class");
        }
        return this.resourceName;
    }

    public void permuteName(List<String> resourceNames, String simpleName) {
        resourceNames.add(simpleName + ".html");
        resourceNames.add(simpleName + ".htm");
        resourceNames.add(simpleName + ".xml");
    }

    public boolean tryResourceName(ResourceFinder finder, String resourceName) {
        try {
            return finder.getResourceAsURL(resourceName).getContent() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void setValueTranslator(ValueTranslator valueTranslator) {
        this.valueTranslator = valueTranslator;
    }
    public interface ValueTranslator {
        <T extends TestElement<T>> Object valueOf(T element, Class asClass, Object baseValue);
    }

    private class ValueTranslatingEvaluationContext<C extends EvaluationContext<C>> implements EvaluationContext<ValueTranslatingEvaluationContext<C>> {
        private C baseContext;

        public ValueTranslatingEvaluationContext(C ognlEvaluationContext) {
            this.baseContext = ognlEvaluationContext;
        }

        public ValueTranslatingEvaluationContext subContext() {
            return new ValueTranslatingEvaluationContext(baseContext.subContext());
        }

        public <T extends TestElement<T>> Object eval(String expression, T element) {
            return baseContext.eval(expression, element);
        }

        public <T extends TestElement<T>> Object set(String expression, T element) {
            return baseContext.set(expression, element);
        }

        public <T extends TestElement<T>> Iterable<ValueTranslatingEvaluationContext<C>> iterate(String expression, T element) {
			return new WrappingIterable<C, ValueTranslatingEvaluationContext<C>>(baseContext.iterate(expression, element)) {
				protected ValueTranslatingEvaluationContext<C> wrap(C base) {
					return new ValueTranslatingEvaluationContext<C>(base);
				}
			};
        }

        public Object getVariable(String name) {
            return baseContext.getVariable(name);
        }

        public void setVariable(String name, Object value) {
            baseContext.setVariable(name, value);
        }

        public <T extends TestElement<T>> Object getValue(T element, Class asClass) {
            Object result = baseContext.getValue(element, asClass);
            return (valueTranslator != null) ? valueTranslator.valueOf(element, asClass, result) : result;
        }

        public IgnoreState getIgnoreState() {
            return baseContext.getIgnoreState();
        }

        public ValueTranslatingEvaluationContext<C> withIgnoreState(IgnoreState ignoreState) {
            return new ValueTranslatingEvaluationContext(baseContext.withIgnoreState(ignoreState));
        }
    }
}

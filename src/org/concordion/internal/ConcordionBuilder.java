package org.concordion.internal;

import org.concordion.Concordion;
import org.concordion.api.*;
import org.concordion.internal.command.*;
import org.concordion.internal.listener.*;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.IOUtil;

import java.io.File;

public class ConcordionBuilder {

    public static final String NAMESPACE_CONCORDION_2007    = "http://www.concordion.org/2007/concordion";
    public static final String NAMESPACE_CONCORDION_OLD     = "http://concordion.org";
    public static final String NAMESPACE_CONCORDION_ANCIENT = "http://concordion.org/namespace/concordion-1.0";

    private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";
    private static final String EMBEDDED_STYLESHEET_RESOURCE = "/org/concordion/internal/resource/embedded.css";

    private SpecificationLocator specificationLocator = new ClassNameBasedSpecificationLocator();
    private Source source = new ClassPathSource();
    private Target target = null;
    private CommandRegistry commandRegistry = new CommandRegistry();
    private DocumentParser documentParser = new DocumentParser(commandRegistry);
    private SpecificationReader specificationReader;
    private EvaluatorFactory evaluatorFactory = new OgnlValidatingEvaluator.Factory();
    private SpecificationCommand specificationCommand = new SpecificationCommand();
    private AssertEqualsCommand assertEqualsCommand = new AssertEqualsCommand();
    private ExecuteCommand executeCommand = new ExecuteCommand();
    private VerifyRowsCommand verifyRowsCommand = new VerifyRowsCommand(documentParser);
    private File baseOutputDir;
    private ThrowableCaughtPublisher throwableListenerPublisher = new ThrowableCaughtPublisher();
    private AssertBooleanCommand assertTrueCommand = new AssertBooleanCommand(true);
    private AssertBooleanCommand assertFalseCommand = new AssertBooleanCommand(false);
    ThrowableCatchingDecorator throwableCatchingDecorator = new ThrowableCatchingDecorator();

    {
        throwableListenerPublisher.addThrowableListener(new ThrowableRenderer());

        withApprovedCommand("", "specification", specificationCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "set", new SetCommand());
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "assertEquals", assertEqualsCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "verifyRows", verifyRowsCommand);

        withApprovedCommand(NAMESPACE_CONCORDION_OLD, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_OLD, "set", new SetCommand());
        withApprovedCommand(NAMESPACE_CONCORDION_OLD, "assertEquals", assertEqualsCommand);

        withApprovedCommand(NAMESPACE_CONCORDION_OLD, "assertFalse", assertFalseCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_OLD, "assertTrue",  assertTrueCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_OLD, "forEach",     verifyRowsCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_OLD, "insertText",  new InsertTextCommand());

        withApprovedCommand(NAMESPACE_CONCORDION_ANCIENT, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_ANCIENT, "param",   new SetCommand());
        withApprovedCommand(NAMESPACE_CONCORDION_ANCIENT, "verify",  assertEqualsCommand);

        withCommandDecorator(throwableCatchingDecorator);
        withCommandDecorator(new LocalValueAndHrefDecorator());
        withCommandDecorator(new LocalTextDecorator());

        throwableCatchingDecorator.addThrowableListener(throwableListenerPublisher);
        assertEqualsCommand.addAssertEqualsListener(new AssertEqualsResultRenderer());
        verifyRowsCommand.addVerifyRowsListener(new VerifyRowsResultRenderer());
        documentParser.addDocumentParsingListener(new DocumentStructureImprover());
        String stylesheetContent = IOUtil.readResourceAsString(EMBEDDED_STYLESHEET_RESOURCE);
        documentParser.addDocumentParsingListener(new StylesheetEmbedder(stylesheetContent));
        assertTrueCommand.addAssertBooleanListener(new AssertBooleanResultRenderer());
        assertFalseCommand.addAssertBooleanListener(new AssertBooleanResultRenderer());
    }

    public ConcordionBuilder withSource(Source source) {
        this.source = source;
        return this;
    }

    public ConcordionBuilder withTarget(Target target) {
        this.target = target;
        return this;
    }

    public ConcordionBuilder withEvaluatorFactory(EvaluatorFactory evaluatorFactory) {
        this.evaluatorFactory = evaluatorFactory;
        return this;
    }

    public ConcordionBuilder withThrowableListener(ThrowableCaughtListener throwableListener) {
        throwableListenerPublisher.addThrowableListener(throwableListener);
        return this;
    }

    public ConcordionBuilder withAssertEqualsListener(AssertEqualsListener listener) {
        assertEqualsCommand.addAssertEqualsListener(listener);
        return this;
    }

    private ConcordionBuilder withApprovedCommand(String namespaceURI, String commandName, Command command) {
        commandRegistry.register(namespaceURI, commandName, command);
        return this;
    }

    public ConcordionBuilder withCommand(String namespaceURI, String commandName, Command command) {
        Check.notEmpty(namespaceURI, "Namespace URI is mandatory");
        Check.notEmpty(commandName, "Command name is mandatory");
        Check.notNull(command, "Command is null");
        Check.isFalse(namespaceURI.contains("concordion.org"),
                "The namespace URI for user-contributed command '" + commandName + "' "
              + "must not contain 'concordion.org'. Use your own domain name instead.");
        return withApprovedCommand(namespaceURI, commandName, command);
    }

    public ConcordionBuilder withCommandDecorator(CommandDecorator decorator) {
        commandRegistry.addDecorator(decorator);
        return this;
    }

    public Concordion build() {
        if (target == null) {
            target = new FileTarget(getBaseOutputDir());
        }
        XMLParser xmlParser = new XMLParser();

        specificationCommand.addSpecificationListener(new BreadcrumbRenderer(source, xmlParser));
        specificationCommand.addSpecificationListener(new PageFooterRenderer(target));
        specificationCommand.addSpecificationListener(new SpecificationExporter(target));

        specificationReader = new XMLSpecificationReader(source, xmlParser, documentParser);

        return new Concordion(specificationLocator, specificationReader, evaluatorFactory);
    }

    private File getBaseOutputDir() {
        if (baseOutputDir != null) {
            return baseOutputDir;
        }
        String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
        if (outputPath == null) {
            return new File(System.getProperty("java.io.tmpdir"), "concordion");
        }
        return new File(outputPath);
    }


}

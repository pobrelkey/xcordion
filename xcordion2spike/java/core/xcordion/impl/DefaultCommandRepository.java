package xcordion.impl;

import xcordion.impl.command.*;
import xcordion.api.*;
import xcordion.util.Coercions;

import java.util.HashMap;

public class DefaultCommandRepository implements CommandRepository {

    private HashMap<String, HashMap<String, CommandFactory>> commandFactories = new HashMap<String, HashMap<String, CommandFactory>>();

    protected AssertBooleanCommand assertFalseCommand, assertTrueCommand;
    protected AssertContainsCommand assertContainsCommand, assertDoesNotContainCommand;
    protected AssertEqualsCommand assertEqualsCommand;
    protected ExecCommand execCommand;
    protected ForEachCommand forEachCommand;
    protected InsertTextCommand insertTextCommand;
    protected SetCommand setCommand;
    protected TableExecuteCommand tableExecuteCommand;
    protected TableForEachCommand tableForEachCommand;

    // TODO: no mention of concordion outside of java-impl
    public static final String NAMESPACE_XCORDION           = "urn:xcordion:v1";
    public static final String NAMESPACE_CONCORDION_2007    = "http://www.concordion.org/2007/concordion";
    public static final String NAMESPACE_CONCORDION_OLD     = "http://concordion.org";
    public static final String NAMESPACE_CONCORDION_ANCIENT = "http://concordion.org/namespace/concordion-1.0";

    public DefaultCommandRepository() {
        this.assertFalseCommand = new AssertBooleanCommand(false);
        this.assertTrueCommand = new AssertBooleanCommand(true);
        this.assertContainsCommand = new AssertContainsCommand(false);
        this.assertDoesNotContainCommand = new AssertContainsCommand(true);
        this.assertEqualsCommand = new AssertEqualsCommand();
        this.execCommand = new ExecCommand();
        this.forEachCommand = new ForEachCommand();
        this.insertTextCommand = new InsertTextCommand();
        this.setCommand = new SetCommand();
        this.tableExecuteCommand = new TableExecuteCommand();
        this.tableForEachCommand = new TableForEachCommand();

        addRegularCommand(NAMESPACE_XCORDION, "isEqual",        assertEqualsCommand);
        addRegularCommand(NAMESPACE_XCORDION, "isFalse",        assertFalseCommand);
        addRegularCommand(NAMESPACE_XCORDION, "isTrue",         assertTrueCommand);
        addRegularCommand(NAMESPACE_XCORDION, "contains",       assertContainsCommand);
        addRegularCommand(NAMESPACE_XCORDION, "doesNotContain", assertDoesNotContainCommand);
        addRegularCommand(NAMESPACE_XCORDION, "set",            setCommand);
        addRegularCommand(NAMESPACE_XCORDION, "show",           insertTextCommand);
        addTableCommand(NAMESPACE_XCORDION, "do",  tableExecuteCommand, execCommand);
        addTableCommand(NAMESPACE_XCORDION, "for", tableForEachCommand, forEachCommand);

        addRegularCommand(NAMESPACE_CONCORDION_2007, "assertEquals",   assertEqualsCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "assertFalse",    assertFalseCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "assertTrue",     assertTrueCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "assertContains", assertContainsCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "set",            setCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "insertText",     insertTextCommand);
        addTableCommand(NAMESPACE_CONCORDION_2007, "execute",    tableExecuteCommand, execCommand);
        addTableCommand(NAMESPACE_CONCORDION_2007, "verifyRows", tableForEachCommand, forEachCommand);

        addRegularCommand(NAMESPACE_CONCORDION_OLD, "assertEquals",   assertEqualsCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "assertFalse",    assertFalseCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "assertTrue",     assertTrueCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "assertContains", assertContainsCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "set",            setCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "insertText",     insertTextCommand);
        addTableCommand(NAMESPACE_CONCORDION_OLD, "execute", tableExecuteCommand, execCommand);
        addTableCommand(NAMESPACE_CONCORDION_OLD, "forEach", tableForEachCommand, forEachCommand);

        addRegularCommand(NAMESPACE_CONCORDION_ANCIENT, "param",   setCommand);
        addRegularCommand(NAMESPACE_CONCORDION_ANCIENT, "verify",  assertEqualsCommand);
        addTableCommand(NAMESPACE_CONCORDION_ANCIENT, "execute", tableExecuteCommand, execCommand);
    }

    private void addRegularCommand(String namespaceUri, String name, Command command) {
        addCommand(namespaceUri, name, new DefaultCommandFactory(command));
    }

    private void addTableCommand(String namespaceUri, String name, Command tableCommand, Command command) {
        addCommand(namespaceUri, name, new TableCommandFactory(tableCommand, command));
    }

    public void addCommand(String namespaceUri, String elementName, CommandFactory command) {
        if (!commandFactories.containsKey(namespaceUri)) {
            commandFactories.put(namespaceUri, new HashMap<String, CommandFactory>());
        }
        commandFactories.get(namespaceUri).put(elementName, command);
    }

    public <T extends TestElement<T>> CommandAndExpression commandForElement(T element) {
        for (TestAttribute attrib : element.getAttributes()) {
            HashMap<String, CommandFactory> commandFactoriesForNamespace = commandFactories.get(attrib.getNamespaceUri());
            if (commandFactoriesForNamespace == null) {
                continue;
            }

            String localName = Coercions.camelCase(attrib.getLocalName());
            CommandFactory factory = commandFactoriesForNamespace.get(localName);
            if (factory == null) {
                continue;
            }

            Command command = factory.commandForElement(element);
            if (command != null) {
                return new CommandAndExpression(command, attrib.getValue());
            }
        }
        return null;
     }


}

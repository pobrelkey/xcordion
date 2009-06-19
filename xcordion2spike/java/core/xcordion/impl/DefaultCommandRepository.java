package xcordion.impl;

import xcordion.impl.command.*;
import xcordion.api.*;
import xcordion.util.Coercions;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class DefaultCommandRepository implements CommandRepository {

    private HashMap<String, HashMap<String, CommandFactory>> commandFactories = new HashMap<String, HashMap<String, CommandFactory>>();
    private HashMap<String, HashMap<String, Pragma>> pragmas = new HashMap<String, HashMap<String, Pragma>>();

    // TODO: no mention of concordion outside of java-impl
    public static final String NAMESPACE_XCORDION           = "urn:xcordion:v1";
    public static final String NAMESPACE_CONCORDION_2007    = "http://www.concordion.org/2007/concordion";
    public static final String NAMESPACE_CONCORDION_OLD     = "http://concordion.org";
    public static final String NAMESPACE_CONCORDION_ANCIENT = "http://concordion.org/namespace/concordion-1.0";

    public DefaultCommandRepository() {
        AssertBooleanCommand assertFalseCommand = new AssertBooleanCommand(false);
        AssertBooleanCommand assertTrueCommand = new AssertBooleanCommand(true);
        AssertContainsCommand assertContainsCommand = new AssertContainsCommand(false);
        AssertContainsCommand assertDoesNotContainCommand = new AssertContainsCommand(true);
        AssertEqualsCommand assertEqualsCommand = new AssertEqualsCommand();
        ExecCommand execCommand = new ExecCommand();
        ForEachCommand forEachCommand = new ForEachCommand();
        InsertTextCommand insertTextCommand = new InsertTextCommand();
        SetCommand setCommand = new SetCommand();
        TableExecuteCommand tableExecuteCommand = new TableExecuteCommand();
        TableForEachCommand tableForEachCommand = new TableForEachCommand();
        IgnorePragma ignorePragma = new IgnorePragma();

        addRegularCommand(NAMESPACE_XCORDION, "isEqual", assertEqualsCommand);
        addRegularCommand(NAMESPACE_XCORDION, "isFalse", assertFalseCommand);
        addRegularCommand(NAMESPACE_XCORDION, "isTrue", assertTrueCommand);
        addRegularCommand(NAMESPACE_XCORDION, "contains", assertContainsCommand);
        addRegularCommand(NAMESPACE_XCORDION, "doesNotContain", assertDoesNotContainCommand);
        addRegularCommand(NAMESPACE_XCORDION, "set", setCommand);
        addRegularCommand(NAMESPACE_XCORDION, "show", insertTextCommand);
        addTableCommand(NAMESPACE_XCORDION, "do", tableExecuteCommand, execCommand);
        addTableCommand(NAMESPACE_XCORDION, "for", tableForEachCommand, forEachCommand);
        addPragma(NAMESPACE_XCORDION, "ignore", ignorePragma);

        addRegularCommand(NAMESPACE_CONCORDION_2007, "assertEquals", assertEqualsCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "assertFalse", assertFalseCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "assertTrue", assertTrueCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "assertContains", assertContainsCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "set", setCommand);
        addRegularCommand(NAMESPACE_CONCORDION_2007, "insertText", insertTextCommand);
        addTableCommand(NAMESPACE_CONCORDION_2007, "execute", tableExecuteCommand, execCommand);
        addTableCommand(NAMESPACE_CONCORDION_2007, "verifyRows", tableForEachCommand, forEachCommand);
        addPragma(NAMESPACE_CONCORDION_2007, "ignore", ignorePragma);

        addRegularCommand(NAMESPACE_CONCORDION_OLD, "assertEquals", assertEqualsCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "assertFalse", assertFalseCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "assertTrue", assertTrueCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "assertContains", assertContainsCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "set", setCommand);
        addRegularCommand(NAMESPACE_CONCORDION_OLD, "insertText", insertTextCommand);
        addTableCommand(NAMESPACE_CONCORDION_OLD, "execute", tableExecuteCommand, execCommand);
        addTableCommand(NAMESPACE_CONCORDION_OLD, "forEach", tableForEachCommand, forEachCommand);
        addPragma(NAMESPACE_CONCORDION_OLD, "ignore", ignorePragma);

        addRegularCommand(NAMESPACE_CONCORDION_ANCIENT, "param", setCommand);
        addRegularCommand(NAMESPACE_CONCORDION_ANCIENT, "verify", assertEqualsCommand);
        addTableCommand(NAMESPACE_CONCORDION_ANCIENT, "execute", tableExecuteCommand, execCommand);
        addPragma(NAMESPACE_CONCORDION_ANCIENT, "ignore", ignorePragma);

    }

    private void addRegularCommand(String namespaceUri, String name, Command command) {
        addCommand(namespaceUri, name, new DefaultCommandFactory(command));
    }

    private void addTableCommand(String namespaceUri, String name, Command tableCommand, Command command) {
        addCommand(namespaceUri, name, new TableCommandFactory(tableCommand, command));
    }

    private void addPragma(String namespaceUri, String elementName, Pragma pragma) {
        if (!pragmas.containsKey(namespaceUri)) {
            pragmas.put(namespaceUri, new HashMap<String, Pragma>());
        }
        pragmas.get(namespaceUri).put(elementName, pragma);
    }

    public void addCommand(String namespaceUri, String elementName, CommandFactory command) {
        if (!commandFactories.containsKey(namespaceUri)) {
            commandFactories.put(namespaceUri, new HashMap<String, CommandFactory>());
        }
        commandFactories.get(namespaceUri).put(elementName, command);
    }

    public <T extends TestElement<T>> ItemAndExpression<Command> commandForElement(T element, IgnoreState ignoreState) {
        if (ignoreState == IgnoreState.OMITTED) {
            return null;
        }
        
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
                return new ItemAndExpression<Command>(command, attrib.getValue());
            }
        }
        return null;
     }

    public <T extends TestElement<T>> List<ItemAndExpression<Pragma>> pragmasForElement(T element) {
        ArrayList<ItemAndExpression<Pragma>> result = new ArrayList<ItemAndExpression<Pragma>>();
        for (TestAttribute attrib : element.getAttributes()) {
            HashMap<String, Pragma> pragmasForNamespace = pragmas.get(attrib.getNamespaceUri());
            if (pragmasForNamespace == null) {
                continue;
            }

            String localName = Coercions.camelCase(attrib.getLocalName());
            Pragma pragma = pragmasForNamespace.get(localName);
            if (pragma != null) {
                result.add(new ItemAndExpression<Pragma>(pragma, attrib.getValue()));
            }
        }
        return result;
    }
}

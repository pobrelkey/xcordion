package xcordion.impl;

import xcordion.api.*;
import xcordion.impl.command.NoOpCommand;

public class XcordionImpl<T extends TestElement<T>> implements Xcordion<T> {
	
	private CommandRepository commandRepository;
	private XcordionEvents<T> broadcaster;

    public XcordionImpl(CommandRepository commandRepository, XcordionEvents<T> broadcaster) {
        this.commandRepository = commandRepository;
        this.broadcaster = broadcaster;

		// DONE: set
		// DONE: execute
		// DONE: InsertText
		// DONE: assertTrue/assertFalse
		// DONE: assertEquals
		// DONE: table execute
		// DONE: table forEach
		// DONE: forEach
		// DONE: contains

        // TODO:
        // ignore
		// stats
		// expectedToPass
		// lang
		// breadcrumbs
		// include
		// run
	}
	
	public <C extends EvaluationContext<C>> void run(TestDocument<T> doc, C rootContext) {
		T bodyElement = doc.getRootElement().getFirstChildNamed("body");
		if (bodyElement == null) {
			bodyElement = doc.getRootElement();
		}

        broadcaster.begin(bodyElement);

        CommandAndExpression command = commandRepository.commandForElement(bodyElement);
		if (command == null) {
			new NoOpCommand().runElementAndChildren(this, bodyElement, rootContext, null);
		} else {
			command.command.runElementAndChildren(this, bodyElement, rootContext, command.expression);
		}

        broadcaster.end(bodyElement);
    }

	public CommandRepository getCommandRepository() {
		return commandRepository;
	}

	public XcordionEvents<T> getBroadcaster() {
		return broadcaster;
	}	

}

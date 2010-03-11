package xcordion.impl;

import xcordion.api.TestElement;
import xcordion.api.Command;

public interface CommandFactory {
	<T extends TestElement<T>> Command commandForElement(T element);
}

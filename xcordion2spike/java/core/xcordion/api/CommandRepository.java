package xcordion.api;

public interface CommandRepository {

	<T extends TestElement<T>> CommandAndExpression commandForElement(T element);
	
}


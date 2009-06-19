package xcordion.api;

public interface CommandRepository {

	<T extends TestElement<T>> CommandAndExpression commandForElement(T element);

    // TODO: method to scan an element for pragmas, evaluate them?+++++
}

    
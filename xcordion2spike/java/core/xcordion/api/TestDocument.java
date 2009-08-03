package xcordion.api;

/**
 * Provides a language/implementation-neutral abstraction of an XML document.
 * @param <T> type of the corresponding concrete implementation of the TestElement interface
 */
public interface TestDocument<T extends TestElement<T>> {
	T getRootElement();
    T newElement(String name);
    T newElement(String namespaceUri, String name);
    String asXml();
}

package xcordion.api;

public interface TestDocument<T extends TestElement<T>> {
	T getRootElement();
    T newElement(String name);
    T newElement(String namespaceUri, String name);
    String asXml();
}

package xcordion.api;

import java.util.List;

/**
 * Provides a language/implementation-neutral abstraction of an XML element.  
 * @param <T> type of a concrete implementation of this interface
 */
public interface TestElement<T extends TestElement<T>> {
	TestDocument<T> getDocument();
	List<T> getChildren();
	Integer getStartLine();
    String getAttribute(String name);
    String getAttribute(String namespaceUri, String name);
    T setAttribute(String name, String value);
    T setAttribute(String namespaceUri, String name, String value);

    /**
     * Returns the text content of this element and all children.  Note that this method will continue
     * to return the original text content of the element even after changes are made during the test.
     * @return The ORIGINAL value of this element, before any changes made over the course of the test
     */
	String getValue();

	String getLocalName();
    T addChild(String namespaceUri, String name);
    T addChild(String name);
	T getParent();

    /**
     * Gets the integer value of an attribute on the element having the specified local name and no namespace.
     * If no value or an unparseable value is specified, returns 1.
     * @param name the local name of the attribute to be read
     * @return value of this attribute as an integer, or 1 if the value cannot be read/parsed
     */
    int getIntAttribute(String name);

    /**
     * Gets the integer value of an attribute on the element having the specified namespace and local name.
     * If no value or an unparseable value is specified, returns 1.
     * @param namespaceUri URI of the namespace of the attribute to be read
     * @param name the local name of the attribute to be read
     * @return value of this attribute as an integer, or 1 if the value cannot be read/parsed
     */
    int getIntAttribute(String namespaceUri, String name);

	T duplicate();
	T insertChildAfter(T sibling, T toBeInserted);
	T remove(T childElement);
    T setText(String text);
    String asXml();
    List<TestAttribute> getAttributes();
    T addStyleClass(String styleClass);
    T appendNonBreakingSpaceIfBlank();
    T appendChild(T child);
    T prependChild(T child);
    T appendText(String text);
    T moveContentTo(T sibling);
    T getFirstChildNamed(String localName);
}

package xcordion.api;

import java.util.List;

public interface TestElement<T extends TestElement<T>> {
	TestDocument<T> getDocument();
	List<T> getChildren();
	Integer getStartLine();
    String getAttribute(String name);
    String getAttribute(String namespaceUri, String name);
    T setAttribute(String name, String value);
    T setAttribute(String namespaceUri, String name, String value);
	String getValue();
	String getLocalName();
    T addChild(String namespaceUri, String name);
    T addChild(String name);
	T getParent();
    int getIntAttribute(String name);
    int getIntAttribute(String namespaceUri, String name);
	T duplicate();
	T insertChildAfter(T sibling, T toBeInserted);
	T remove(T placeholder);
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

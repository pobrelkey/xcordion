package xcordion.api;

/**
 * Provides a language/implementation-neutral abstraction of an XML attribute.
 */
public interface TestAttribute {
    String getNamespaceUri();
    String getLocalName();
    String getValue();
}

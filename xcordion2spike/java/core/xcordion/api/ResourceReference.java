package xcordion.api;

import xcordion.api.TestElement;

public class ResourceReference<T extends TestElement<T>> {
    private T element;
    private String attribute;
    private String resourcePath;

    public ResourceReference(T element, String attribute, String resourcePath) {
        this.element = element;
        this.attribute = attribute;
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourceReferenceUri(String uri) {
        element.setAttribute(attribute, uri);
    }
}

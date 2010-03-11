package xcordion.lang.java;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.Attribute;
import org.jdom.Text;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import xcordion.api.TestDocument;
import xcordion.api.TestElement;
import xcordion.api.TestAttribute;
import xcordion.util.XcordionBug;

import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.StringWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class JDomTestDocument implements TestDocument<JDomTestDocument.JDomTestElement> {

    private static Class LINE_NUMBER_ELEMENT_CLASS;
    private static Method GET_START_LINE_METHOD;
    static {
        try {
            LINE_NUMBER_ELEMENT_CLASS = Class.forName("org.jdom.contrib.input.LineNumberElement");
            GET_START_LINE_METHOD = LINE_NUMBER_ELEMENT_CLASS.getMethod("getStartLine");
        } catch (Exception e) {
            // oh well
        }
    }

	private Document doc;
    private int namespaceCounter = 0;
    private HashMap<Element, JDomTestElement> elementCache = new HashMap<Element, JDomTestElement>();

	public JDomTestDocument(Document doc) {
		this.doc = doc;
	}

	public JDomTestElement getRootElement() {
		return wrap(doc.getRootElement());
	}

    public JDomTestElement newElement(String name) {
        return newElement(null, name);
    }

    public JDomTestElement newElement(String namespaceUri, String name) {
        return wrap(new Element(name, Namespace.getNamespace(namespaceUri)));
    }

    public String asXml() {
        StringWriter sw = new StringWriter();
        try {
            new XMLOutputter(Format.getRawFormat().setOmitEncoding(true)).output(doc, sw);
        } catch (IOException e) {
            throw new XcordionBug("StringWriter threw an IOException - check out window for flying pigs", e);
        }
        return sw.toString();
    }

    public void write(OutputStream st) throws IOException {
        new XMLOutputter(Format.getRawFormat()).output(doc, st);
    }

    private synchronized JDomTestElement wrap(Element e) {
        if (e == null) {
            return null;
        }
        JDomTestElement element = elementCache.get(e);
		if (element == null) {
			element = new JDomTestElement(e);
			elementCache.put(e, element);
		}
		return element;
	}

    public class JDomTestElement implements TestElement<JDomTestElement> {

		private Element element;
		private String value;
		private boolean valuePopulated;

		private JDomTestElement(Element element) {
			this.element = element;
		}

		public List<JDomTestElement> getChildren() {
            ArrayList<JDomTestElement> result = new ArrayList<JDomTestElement>();
            for (Object o : element.getChildren()) {
                if (o instanceof Element) {
                    result.add(wrap((Element) o));
                }
            }
            return Collections.unmodifiableList(result);
		}

		public JDomTestDocument getDocument() {
			return JDomTestDocument.this;
		}

		public Integer getStartLine() {
			// return (element instanceof LineNumberElement) ? ((LineNumberElement) element).getStartLine() : null;
            if (LINE_NUMBER_ELEMENT_CLASS != null && LINE_NUMBER_ELEMENT_CLASS.isAssignableFrom(element.getClass())) {
                try {
                    return (Integer) GET_START_LINE_METHOD.invoke(element);
                } catch (IllegalAccessException e) {
                    throw new XcordionBug(e);
                } catch (InvocationTargetException e) {
                    throw new XcordionBug(e);
                }
            }
            return null;
		}

        public String getAttribute(String name) {
            return getAttribute(null, name);
        }

        public String getAttribute(String namespaceUri, String elementName) {
            Namespace namespace = namespace(namespaceUri, false);
            return (namespace != null) ? element.getAttributeValue(elementName, namespace) : null;
		}

        public JDomTestElement setAttribute(String name, String value) {
            setAttribute(null, name, value);
            return this;
        }

        private Namespace namespace(String namespaceUri, boolean createIfNotFound) {
            if (namespaceUri != null && namespaceUri.length() > 0) {
                for (Parent x = element; x instanceof Element; x = x.getParent()) {
                    Element e = (Element) x;
                    if (e.getNamespace().getURI().equals(namespaceUri)) {
                        return e.getNamespace();
                    } else {
                        for (Object n : e.getAdditionalNamespaces()) {
                            if (((Namespace) n).getURI().equals(namespaceUri)) {
                                return (Namespace) n;
                            }
                        }
                    }
                }
                if (createIfNotFound) {
                    Namespace namespace = Namespace.getNamespace("ns" + (namespaceCounter++), namespaceUri);
                    doc.getRootElement().addNamespaceDeclaration(namespace);
                    return namespace;
                }
                return null;
            }
            return Namespace.NO_NAMESPACE;
        }

        public JDomTestElement setAttribute(String namespaceUri, String name, String value) {
            element.setAttribute(name, value, namespace(namespaceUri, true));
            return this;
        }

        public String getValue() {
            populateValue();
			return value;
		}

		public String getLocalName() {
			return element.getName();
		}

        public JDomTestElement addChild(String namespaceUri, String name) {
        	populateValue();
            Namespace namespace = namespace(namespaceUri, true);
            Element newChild = new Element(name, namespace);
            element.addContent(newChild);
            return wrap(newChild);
        }

        public JDomTestElement addChild(String name) {
            return addChild(null, name);
		}

		public JDomTestElement getParent() {
			Parent parent = element.getParent();
			return (parent != null && parent instanceof Element) ? wrap((Element) parent) : null;
		}

        public int getIntAttribute(String name) {
            return getIntAttribute(null, name);
        }

        public int getIntAttribute(String namespace, String name) {
		    String stringValue = getAttribute(namespace, name);
		    int result = 1;
		    if (stringValue != null && stringValue.length() > 0) {
		        try {
		            result = Integer.parseInt(stringValue);
		        } catch (NumberFormatException ignored) {
		        }
		    }
		    return result;
		}

		public JDomTestElement duplicate() {
			return wrap((Element) element.clone());
		}

		public JDomTestElement insertChildAfter(JDomTestElement sibling, JDomTestElement toBeInserted) {
            populateValue();
            int index = element.indexOf(sibling.element);
			if (index == -1) {
				throw new IllegalArgumentException("refrence element is not a child of this element");
			}
			element.addContent(index+1, toBeInserted.element);
            return this;
        }

		public JDomTestElement remove(JDomTestElement child) {
            populateValue();
			element.removeContent(child.element);
			//elementCache.remove(child.element);
            return this;
        }

        public JDomTestElement setText(String text) {
            populateValue();
            element.setText(text);
            return this;
        }

        private void populateValue() {
            if (!valuePopulated) {
                value = element.getValue();
                valuePopulated = true;
                JDomTestElement parent = getParent();
                if (parent != null) {
                    parent.populateValue();
                }
            }
        }

        public String asXml() {
            StringWriter sw = new StringWriter();
            try {
                new XMLOutputter(Format.getRawFormat()).output(element, sw);
            } catch (IOException e) {
                throw new XcordionBug("StringWriter threw an IOException - check out window for flying pigs", e);
            }
            return sw.toString();
        }

        public List<TestAttribute> getAttributes() {
            ArrayList<TestAttribute> result = new ArrayList<TestAttribute>();
            for (Attribute a : (List<Attribute>) element.getAttributes()) {
                result.add(new SimpleTestAttribute(a));
            }
            return Collections.unmodifiableList(result);
        }

		public JDomTestElement addStyleClass(String styleClass) {
			String classes = element.getAttributeValue("class");
			if (classes == null || classes.length() == 0) {
				classes = styleClass;
			} else if (!classes.matches("\\b" + styleClass + "\\b")) {
				classes += ' ' + styleClass;
			}
			element.setAttribute("class", classes);
			return this;
		}

		public JDomTestElement appendNonBreakingSpaceIfBlank() {
			String text = element.getText();
			if (text == null || text.trim().length() == 0) {
				element.addContent(new Text("\u00A0"));
			}
			return this;
		}

		public JDomTestElement appendChild(JDomTestElement child) {
			element.addContent(child.element);
			return this;
		}

		public JDomTestElement prependChild(JDomTestElement child) {
			element.addContent(0, child.element);
			return this;
		}

		public JDomTestElement appendText(String text) {
			element.addContent(new Text(text));
			return this;
		}

		public JDomTestElement moveContentTo(JDomTestElement sibling) {
			for (int i = element.getContentSize(); i > 0; i--) {
				sibling.element.addContent(element.removeContent(0));
			}
			return this;
		}

        public JDomTestElement getFirstChildNamed(String localName) {
            return wrap(element.getChild(localName));
        }

    }

    private class SimpleTestAttribute implements TestAttribute {
    	private Attribute a;

        private SimpleTestAttribute(Attribute a) {
        	this.a = a;
        }

        public String getNamespaceUri() {
            return a.getNamespaceURI();
        }

        public String getLocalName() {
            return a.getName();
        }

        public String getValue() {
            return a.getValue();
        }
    }

}

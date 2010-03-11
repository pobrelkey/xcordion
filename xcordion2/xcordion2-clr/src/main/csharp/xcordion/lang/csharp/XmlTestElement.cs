using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;

using xcordion.api;
using xcordion.util;
using System.Text.RegularExpressions;

namespace xcordion.lang.csharp
{
    public class XmlTestElement : TestElement<XmlTestElement>
    {
        private XmlTestDocument testDoc;
        private XmlElement element;
        private string value;
        private bool valuePopulated;

        internal XmlTestElement(XmlTestDocument d, XmlElement e)
        {
            this.testDoc = d;
            this.element = e;
        }

        public TestDocument<XmlTestElement> getDocument()
        {
            return testDoc;
        }

        public ICompatibleList<XmlTestElement> getChildren()
        {
            SimpleList<XmlTestElement> result = new SimpleList<XmlTestElement>();
            foreach (XmlNode node in element.ChildNodes) {
                XmlElement e = node as XmlElement;
                if (e != null) {
                    result.Add(testDoc.wrap(e));
                }
            }
            return result;
        }

        public int? getStartLine()
        {
            // TODO: will require us to extend the .NET DOM
            IXmlLineInfo lineInfo = element as IXmlLineInfo;
            return (lineInfo != null && lineInfo.HasLineInfo()) ? lineInfo.LineNumber : default(int?);
        }

        public string getAttribute(string name)
        {
            return element.GetAttribute(name);
        }

        public string getAttribute(string namespaceUri, string name)
        {
            return element.GetAttribute(name, namespaceUri);
        }

        public string getValue()
        {
            populateValue();
            return value;
        }

        public string getLocalName()
        {
            return element.LocalName;
        }

        public XmlTestElement addChild(string name)
        {
            populateValue();
            return testDoc.wrap((XmlElement) element.AppendChild(element.OwnerDocument.CreateElement(name)));
        }

        public XmlTestElement getParent()
        {
            XmlElement e = element.ParentNode as XmlElement;
            return (e != null) ? testDoc.wrap(e) : null;
        }

        public int getIntAttribute(string name)
        {
            string value = element.GetAttribute(name);
            return !string.IsNullOrEmpty(value) ? Convert.ToInt32(value) : 1;
        }

        public int getIntAttribute(string namespaceUri, string name)
        {
            string value = getAttribute(namespaceUri, name);
            return !string.IsNullOrEmpty(value) ? Convert.ToInt32(value) : 1;
        }

        public XmlTestElement duplicate()
        {
            return testDoc.wrap((XmlElement) element.CloneNode(true));
        }

        public XmlTestElement insertChildAfter(XmlTestElement sibling, XmlTestElement toBeInserted)
        {
            populateValue();
            element.InsertAfter(toBeInserted.element, sibling.element);
            return this;
        }

        public XmlTestElement remove(XmlTestElement placeholder)
        {
            populateValue();
            element.RemoveChild(placeholder.element);
            return this;
        }

        public XmlTestElement setText(string text)
        {
            populateValue();
            element.InnerText = text;
            return this;
        }

        public XmlTestElement setAttribute(string name, string value)
        {
            element.SetAttribute(name, value);
            return this;
        }

        public XmlTestElement setAttribute(string namespaceUri, string name, string value)
        {
            element.SetAttribute(name, namespaceUri, value);
            return this;
        }

        public string asXml()
        {
            return element.OuterXml;
        }

        public XmlTestElement addChild(string namespaceUri, string name)
        {
            populateValue();
            return testDoc.wrap((XmlElement) element.AppendChild(element.OwnerDocument.CreateElement(name, namespaceUri)));
        }

        private void populateValue()
        {
            if (!valuePopulated)
            {
                value = element.InnerText;
                valuePopulated = true;
                XmlTestElement parent = getParent();
                if (parent != null)
                {
                    parent.populateValue();
                }
            }
        }



        public ICompatibleList<TestAttribute> getAttributes()
        {
            SimpleList<TestAttribute> result = new SimpleList<TestAttribute>();
            foreach (XmlAttribute attrib in element.Attributes)
            {
                result.Add(new XmlTestAttribute(attrib));
            }
            return result;
        }

        public XmlTestElement addStyleClass(string styleClass)
        {
            string classes = element.GetAttribute("class");
            if (string.IsNullOrEmpty(classes))
            {
                classes = styleClass;
            }
            else if (!(new Regex("\\b" + styleClass + "\\b").IsMatch(classes)))
            {
                classes += ' ' + styleClass;
            }
            element.SetAttribute("class", classes);
            return this;
        }

        public XmlTestElement appendNonBreakingSpaceIfBlank()
        {
            string text = element.InnerText;
            if (text == null || text.Trim().Length == 0)
            {
                element.AppendChild(element.OwnerDocument.CreateTextNode("\x00A0"));
            }
            return this;
        }

        public XmlTestElement appendChild(XmlTestElement child)
        {
            element.AppendChild(child.element);
            return this;
        }

        public XmlTestElement prependChild(XmlTestElement child)
        {
            element.PrependChild(child.element);
            return this;
        }

        public XmlTestElement appendText(string text)
        {
            element.AppendChild(element.OwnerDocument.CreateTextNode(text));
            return this;
        }

        public XmlTestElement moveContentTo(XmlTestElement sibling)
        {
            while (element.FirstChild != null) 
            {
                sibling.element.AppendChild(element.RemoveChild(element.FirstChild));
            }
            return this;
        }

        public XmlTestElement getFirstChildNamed(string localName)
        {
            foreach (XmlNode child in element.ChildNodes)
            {
                if (child is XmlElement && ((XmlElement)child).LocalName.Equals(localName))
                {
                    return testDoc.wrap((XmlElement)child);
                }
            }
            return null;
        }
    }

    internal class XmlTestAttribute : TestAttribute 
    {
        private XmlAttribute attrib;

        internal XmlTestAttribute(XmlAttribute attrib)
        {
            this.attrib = attrib;
        }

        public string getNamespaceUri()
        {
            return attrib.NamespaceURI;
        }

        public string getLocalName()
        {
            return attrib.LocalName;
        }

        public string getValue()
        {
            return attrib.Value;
        }
    }
}

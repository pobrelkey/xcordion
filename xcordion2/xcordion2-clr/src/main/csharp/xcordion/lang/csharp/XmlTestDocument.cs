using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;

using xcordion.api;


namespace xcordion.lang.csharp
{
    public class XmlTestDocument : TestDocument<XmlTestElement>
    {
        private XmlDocument doc;
        private Dictionary<XmlElement, XmlTestElement> elementCache = new Dictionary<XmlElement, XmlTestElement>();

        public XmlTestDocument(XmlDocument d)
        {
            this.doc = d;
        }


        public XmlTestElement getRootElement()
        {
            return wrap(doc.DocumentElement);
        }

        internal XmlTestElement wrap(XmlElement xmlElement)
        {
            if (!elementCache.ContainsKey(xmlElement))
            {
                elementCache.Add(xmlElement, new XmlTestElement(this, xmlElement));
            }
            return elementCache[xmlElement];
        }

        public XmlTestElement newElement(string name)
        {
            return wrap(doc.CreateElement(name));
        }

        public XmlTestElement newElement(string namespaceUri, string name)
        {
            return wrap(doc.CreateElement(name, namespaceUri));
        }

        public string asXml()
        {
            return doc.OuterXml;
        }
    }
}

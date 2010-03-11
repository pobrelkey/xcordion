using System;
using System.Collections.Generic;
using System.Text;
using xcordion.impl.events;
using xcordion.impl.theme;
using xcordion.impl;
using System.IO;
using System.Xml;

namespace xcordion.lang.csharp
{
    public class SimpleXcordionRunner
    {
        private object testInstance;
        private string resourceName;
        private XmlTestDocument testDocument;
        private string outputPath;

        public SimpleXcordionRunner(object testInstance)
        {
            this.testInstance = testInstance;
        }

        public void runTest()
        {
            runTest(true);
        }

        public void runTest(bool isExpectedToPass)
        {
            XcordionEventsBroadcaster<XmlTestElement> broadcaster = new XcordionEventsBroadcaster<XmlTestElement>();

            XmlTestDocument testDocument = TestDocument;

            ResultSummariser<XmlTestElement> summary = new ResultSummariser<XmlTestElement>();
            summary.setExpectedToPass(isExpectedToPass);
            broadcaster.addListener(summary);

            ConcordionMarkupTheme<XmlTestElement> theme = new ConcordionMarkupTheme<XmlTestElement>();
            broadcaster.addListener(theme);

            XcordionImpl<XmlTestElement> xcordion = new XcordionImpl<XmlTestElement>(new DefaultCommandRepository(), broadcaster);

            OgnlEvaluationContext ognlEvaluationContext = new OgnlContextFactory().newContext("ognl", testInstance);
            xcordion.run(testDocument, ognlEvaluationContext);


            Type testClass = testInstance.GetType();
            String outputFilePath = new MarkupWriter(OutputDir).Write(testDocument, OutputPath, testClass, theme.getResourceReferences());



            Console.WriteLine("Xcordion result for " + testClass.Name + ": " + summary.getScoreLine());
            String message = summary.getMessage();
            if (!summary.isHappy()) 
            {
                message += " - see output for details: " + outputFilePath;
            }
            else 
            {
                Console.WriteLine(outputFilePath);
            }
            if (!summary.isSuccessful()) 
            {
                // TODO: fail more spectacularly... custom Exception perhaps?
                throw new Exception(message);
            }
        }

        private XmlTestDocument TestDocument
        {
            get
            {
                if (this.testDocument == null)
                {
                    ResourceFinder finder = new ResourceFinder(testInstance.GetType());
                    using (Stream s = finder.GetResource(ResourceName))
                    {
                        XmlDocument doc = new XmlDocument();
                        doc.Load(s);
                        return this.testDocument = new XmlTestDocument(doc);
                    }
                }
                return this.testDocument;
            }
        }

        private string ResourceName
        {
            get 
            {
                if (this.resourceName == null)
                {
                    ResourceFinder finder = new ResourceFinder(testInstance.GetType());
                    List<string> resourceNames = new List<string>();

                    string simpleName = testInstance.GetType().Name;
                    PermuteName(resourceNames, simpleName);
                    if (simpleName.EndsWith("Test"))
                    {
                        PermuteName(resourceNames, simpleName.Substring(0, simpleName.Length - 4));
                    }

                    foreach (string resourceName in resourceNames)
                    {
                        if (TryResourceName(finder, resourceName))
                        {
                            this.resourceName = resourceName;
                            return resourceName;
                        }
                    }

                    throw new FileNotFoundException("Can't find any test resources related to this class");
                }
                return this.resourceName;
            }
        }

        private DirectoryInfo OutputDir
        {
            get
            {
                // TODO: look up configurable value from registry or something
                return new DirectoryInfo(Path.GetTempPath() + Path.DirectorySeparatorChar + "xcordion");
            }
        }

        private String OutputPath
        {
            get
            {
                if (outputPath == null)
                {
                    outputPath = testInstance.GetType().FullName.Replace('.', Path.DirectorySeparatorChar);
                    if (outputPath.IndexOf(Path.DirectorySeparatorChar) != -1)
                    {
                        outputPath = outputPath.Substring(0, outputPath.LastIndexOf(Path.DirectorySeparatorChar)) + Path.DirectorySeparatorChar + ResourceName;
                    }
                    else
                    {
                        outputPath = ResourceName;
                    }
                }
                return outputPath;

            }
        }

        static private void PermuteName(List<string> resourceNames, string simpleName)
        {
            resourceNames.Add(simpleName + ".html");
            resourceNames.Add(simpleName + ".htm");
            resourceNames.Add(simpleName + ".xml");
        }

        static private bool TryResourceName(ResourceFinder finder, string resourceName)
        {
            try
            {
                return finder.ResourceExists(resourceName);
            }
            catch (Exception)
            {
                return false;
            }
        }


    }
}

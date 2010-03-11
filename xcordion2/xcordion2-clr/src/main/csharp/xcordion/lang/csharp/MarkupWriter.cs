using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

using xcordion.api;
using xcordion.util;
using xcordion.lang.csharp;

namespace xcordion.lang.csharp
{
    class MarkupWriter
    {
        private DirectoryInfo outputDirectory;

        public MarkupWriter(DirectoryInfo outputDirectory)
        {
            this.outputDirectory = outputDirectory;
        }

        private void Mkdirs(DirectoryInfo dir)
        {
            if (dir.Parent == null || dir.Parent == dir) 
            {
                throw new ArgumentException();
            }
            if (!dir.Parent.Exists)
            {
                Mkdirs(dir.Parent);
            }
            dir.Create();
        }

        public string Write(XmlTestDocument testDocument, string outputPath, Type testClass, ICompatibleList<ResourceReference<XmlTestElement>> resourceRefs)
        {
            if (!outputDirectory.Exists) {
                outputDirectory.Create();
            }

            foreach (ResourceReference<XmlTestElement> resourceRef in resourceRefs) 
            {
        	    WriteResourceToOutputDirectory(resourceRef, testClass, outputPath);
            }

            FileInfo outputFile = new FileInfo(outputDirectory.FullName + Path.DirectorySeparatorChar + outputPath);
            if (!outputFile.Directory.Exists)
            {
                Mkdirs(outputFile.Directory);
            }

            File.WriteAllText(outputFile.FullName, testDocument.asXml());

            return outputFile.FullName;
        }

        private void WriteResourceToOutputDirectory(ResourceReference<XmlTestElement> resourceRef, Type testClass, String outputPath) {
            string path = resourceRef.getResourcePath();
            Stream resource = new ResourceFinder(testClass).GetResource(path);
            if (resource == null)
            {
                resource = new ResourceFinder(typeof(MarkupWriter)).GetResource(path);
                if (resource == null)
                {
                    StringBuilder sb = new StringBuilder();
                    string[] resourceNames = typeof(MarkupWriter).Assembly.GetManifestResourceNames();
                    sb.AppendLine("resource count: " + resourceNames.Length);
                    foreach (string s in resourceNames) {
                        sb.AppendLine("resource: " + s);
                    }
                    throw new XcordionBug(sb.ToString() + "Cannot find resource: " + path);
                }
            }

            using (resource) 
            {
                path = path.Replace('/', Path.DirectorySeparatorChar);
                while (path[0] == Path.DirectorySeparatorChar)
                {
			        path = path.Substring(1);
		        }

                FileInfo outputFile = new FileInfo(outputDirectory.FullName + Path.DirectorySeparatorChar + path);
                if (!outputFile.Directory.Exists)
                {
                    Mkdirs(outputFile.Directory);
                }

                resourceRef.setResourceReferenceUri(FileUtils.relativePath(outputPath, path, false, Path.DirectorySeparatorChar));

                if (outputFile.Exists && outputFile.LastWriteTime < DateTime.Now.AddSeconds(5)) 
                {
        	        return;
                }

                byte[] bytes = new byte[resource.Length];
                resource.Read(bytes, 0, bytes.Length);
                File.WriteAllBytes(outputFile.FullName, bytes);
            }

        
        }

    }
}

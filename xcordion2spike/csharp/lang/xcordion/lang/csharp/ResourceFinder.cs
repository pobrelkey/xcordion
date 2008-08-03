using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace xcordion.lang.csharp
{
    public class ResourceFinder
    {
        private Type baseType;

        public ResourceFinder(Type baseType)
        {
            this.baseType = baseType;
        }

        public bool ResourceExists(string path)
        {
            return GetManifestResourceName(path) != null;
        }

        public Stream GetResource(string path)
        {
            string resourceName = GetManifestResourceName(path);
            return (resourceName != null) ? baseType.Assembly.GetManifestResourceStream(resourceName) : null;
        }
        
        public string GetManifestResourceName(string path) 
        {
            string targetName = ((path[0] == '/') ? path.Substring(1) : (baseType.Namespace + '.' + path)).Replace('/', '.');
            foreach (string resourceName in baseType.Assembly.GetManifestResourceNames())
            {
                if (resourceName.Equals(targetName) || resourceName.EndsWith('.' + targetName))
                {
                    return resourceName;
                }
            }
            return null;
        }
    }
}

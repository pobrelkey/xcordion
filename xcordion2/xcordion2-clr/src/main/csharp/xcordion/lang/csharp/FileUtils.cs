using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace xcordion.lang.csharp
{
    static class FileUtils
    {

        static public string relativePath(FileSystemInfo from, FileSystemInfo to)
        {
            return relativePath(from, to, Path.DirectorySeparatorChar);
        }

        static public string relativePath(FileSystemInfo from, FileSystemInfo to, char separatorChar)
        {
            string fromPath = from.FullName;
            string toPath = to.FullName;
            bool isDirectory = from is DirectoryInfo;
            return relativePath(fromPath, toPath, isDirectory, separatorChar);
        }

        public static string relativePath(string fromPath, string toPath, bool fromIsDirectory, char separatorChar) 
        {
            List<string> fromElements = splitPath(fromPath);
            List<string> toElements = splitPath(toPath);
            while (fromElements.Count != 0 && toElements.Count != 0) 
            {
                if (fromElements[0] != toElements[0]) 
                {
                    break;
                }
                fromElements.RemoveAt(0);
                toElements.RemoveAt(0);
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < fromElements.Count-(fromIsDirectory ? 0 : 1); i++) 
            {
                result.Append("..");
                result.Append(separatorChar);
            }
            foreach (string s in toElements) 
            {
                result.Append(s);
                result.Append(separatorChar);
            }
            return result.ToString().Substring(0, result.Length-1);
        }

        private static List<string> splitPath(string path)
        {
            List<string> pathElements = new List<string>();
            foreach (string token in path.Split(Path.DirectorySeparatorChar))
            {
                if (token == ".")
                {
                    // do nothing
                }
                else if (token == "..")
                {
                    if (pathElements.Count != 0)
                    {
                        pathElements.RemoveAt(pathElements.Count - 1);
                    }
                }
                else
                {
                    pathElements.Add(token);
                }
            }
            return pathElements;
        }

    }
}

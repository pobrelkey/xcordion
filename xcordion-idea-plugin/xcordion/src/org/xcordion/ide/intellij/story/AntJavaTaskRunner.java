package org.xcordion.ide.intellij.story;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.net.URL;

public class AntJavaTaskRunner {
    private Project project;
    private Java javaTask;

    public AntJavaTaskRunner(ModuleAdapter moduleAdapter, String testClassName, String runnerClassName) {
        project = createProject(testClassName);
        Target target = createTarget(testClassName, runnerClassName);
        project.addTarget(target);
        project.setDefault(target.getName());
        moduleAdapter.setAttributes(this);
    }

    private Project createProject(String testClassName) {
        Project project = new Project();
        project.setName(testClassName);
        project.init();
        return project;
    }

    private Target createTarget(String testClassName, String runnerClassName) {
        Target target = new Target();
        target.setName(testClassName);
        target.addTask(javaTask(testClassName, runnerClassName));
        return target;
    }

    public void execute() {
        new AntProjectExecutor().executeDefaultTarget(project);
    }

    public void addBuildListener(BuildListener buildListener) {
        project.addBuildListener(buildListener);
    }

    public void appendClasspaths(URL[] classpaths) {
        javaTask.createClasspath().append(AntUtils.toPath(project, classpaths));
    }

    private Java javaTask(String testClassName, String runnerClassName) {
        javaTask = (Java) project.createTask("java");
        javaTask.setFork(true);
        javaTask.setNewenvironment(true);
        javaTask.setClassname(runnerClassName);
        javaTask.setFailonerror(true);
        javaTask.createArg().setValue(testClassName);
        return javaTask;
    }

    public void setFileEncoding(String encoding) {
        String encodingArg = "-Dfile.encoding=" + encoding;
        addJvmArgs(encodingArg);
    }
    
    public void addJvmArgs(String args) {
        javaTask.createJvmarg().setLine(args);
    }

    public void setBaseDir(File baseDir) {
        project.setBaseDir(baseDir);
    }

    public void setJvm(String jvm) {
        javaTask.setJvm(jvm);
    }

    public void setJvmVersion(String jvmVersion) {
        javaTask.setJVMVersion(jvmVersion);
    }

    public void setMaxMemory(String maxMemory) {
        javaTask.setMaxmemory(maxMemory);
    }

    public class AntProjectExecutor {
        public void executeDefaultTarget(Project project) {
            Throwable error = null;
            try {
                project.fireBuildStarted();
                project.executeTarget(project.getDefaultTarget());
            } catch (RuntimeException re) {
                error = re;
            } finally {
                project.fireBuildFinished(error);
            }
        }
    }

    public static class AntUtils {
        public static Path toPath(Project antProject, URL[] pathURLs) {
            if (pathURLs == null || pathURLs.length == 0) {
                return null;
            }
            Path path = new Path(antProject, getPath(pathURLs[0]));
            for (int i = 1; i < pathURLs.length; i++) {
                path.append(new Path(antProject, getPath(pathURLs[i])));
            }
            return path;
        }

        private static String getPath(URL url) {
            return new File(url.getFile()).getPath();
        }
    }
}

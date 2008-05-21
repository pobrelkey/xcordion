package org.xcordion.ide.intellij;

import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.testFramework.LightCodeInsightTestCase;
import org.jmock.Mockery;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XcordionReferenceTest extends LightCodeInsightTestCase {
    Mockery context = new Mockery();
    private static final String XCORDION_TEST_CLASS = "org.xcordion.ide.intellij.MyFirstXcordionSpecTestClass";

    @Override
    protected ProjectJdk getProjectJDK() {
        return ProjectJdkTable.getInstance().getInternalJdk();
    }

    public void testReturnsListOfMethodsForTestClass() throws Exception {
        XcordionReference reference = setupXcordionReferenceForAttributeValue("");

        Object[] variants = reference.getVariants();

        assertSameElements(variants, instrospectDistinctMethodNamesForClass(MyFirstXcordionSpecTestClass.class));
    }

    public void testHandleMethodChainingVariants() throws Exception {
        XcordionReference reference = setupXcordionReferenceForAttributeValue("getALong().IntellijIdeaRulezzz ");

        Object[] variants = reference.getVariants();

        assertSameElements(variants, instrospectDistinctMethodNamesForClass(Long.class));
        //Repeat this test for some other attributeValues that return jdk types etc.
    }



    //TODO: delete once passes on Macintosh
    public void testJavaLangObjectSuperMethod() throws Exception {

        PsiClass clazz = PsiManager.getInstance(getProject()).findClass("java.lang.Object", getProject().getAllScope());
        final PsiClass aClass =
                getPsiManager().getElementFactory().createClassFromText("public String toString() {return null;}", null);
        final PsiMethod method = aClass.getMethods()[0];
        final PsiMethod[] superMethods = method.findSuperMethods();
        assertEquals(1, superMethods.length);
        assertEquals("java.lang.Object", superMethods[0].getContainingClass().getQualifiedName());
    }

    private XcordionReference setupXcordionReferenceForAttributeValue(String attributeValue) throws Exception {
        PsiClass psiClass = configurePsiClassFromJavaFile();
        XmlAttribute xmlAttribute = getPsiManager().getElementFactory().createXmlAttribute("concordion:execute", attributeValue);
        XcordionReference reference = new XcordionReference(xmlAttribute.getValueElement(), psiClass);
        return reference;
    }

    private PsiClass configurePsiClassFromJavaFile() throws Exception {
        String absolutePathToFile = System.getProperty("user.dir") + "/test/org/xcordion/ide/intellij/MyFirstXcordionSpecTestClass.java";
        final File ioFile = new File(absolutePathToFile);
        String fileText = new String(FileUtil.loadFileText(ioFile, CharsetToolkit.UTF8));
        fileText = StringUtil.convertLineSeparators(fileText, "\n");
        configureFromFileText(ioFile.getName(), fileText);
        PsiClass psiClass = getPsiManager().findClass(XCORDION_TEST_CLASS, GlobalSearchScope.allScope(getProject()));
        return psiClass;
    }

    private String[] instrospectDistinctMethodNamesForClass(Class<?> clazz) {
        List<String> methods = new ArrayList<String>();

        for (Method method : clazz.getMethods()) {
            if (!methods.contains(method.getName())) {
                methods.add(method.getName());
            }
        }
        return methods.toArray(new String[0]);
    }

    @Test
    public void handlePropertiesInMethodsVariants() {
        //Need to handle properties in methods, i.e., asDate(#somedate) or asDate('2008-05-13', blah)
        fail();
    }

    //Need to handle methods in methods, i.e., asDate(someFunction(#aValue, 'some text'), #bvalue)
    @Test
    public void handleRecursiveMethodsInMethodsVariants() {
        fail();
    }

    //Need to hand ognl comma separator, i.e., build(), doSomethingElse(#now)
    @Test
    public void handleOgnlMethodCommaSeparatorVairants() {
        fail();
    }

    //Add support ognl style getMethods as properties, i.e. getBlah(), can also be referenced as blah
    @Test
    public void handleOgnlStyleGetMethodsAsPropertiesVariants() {
        fail();
    }

    private String getXcordionTestClassFileContents() {
        return "package org.xcordion;\n" +
                "\n" +
                "import java.util.Date;\n" +
                "\n" +
                "\n" +
                "public class MyFirstXcordionSpecTestClass {\n" +
                "    String aString;\n" +
                "    Long aLong;\n" +
                "    int aint;\n" +
                "    Date aDate;\n" +
                "\n" +
                "    public MyFirstXcordionSpecTestClass(String aString, Long aLong, int aint, Date aDate) {\n" +
                "        this.aString = aString;\n" +
                "        this.aLong = aLong;\n" +
                "        this.aint = aint;\n" +
                "        this.aDate = aDate;\n" +
                "    }\n" +
                "\n" +
                "    public void someMethod(){\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    public String someOtherMethod(){\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    public String getAString() {\n" +
                "        return aString;\n" +
                "    }\n" +
                "\n" +
                "    public void setAString(String aString) {\n" +
                "        this.aString = aString;\n" +
                "    }\n" +
                "\n" +
                "    public Long getALong() {\n" +
                "        return aLong;\n" +
                "    }\n" +
                "\n" +
                "    public void setALong(Long aLong) {\n" +
                "        this.aLong = aLong;\n" +
                "    }\n" +
                "\n" +
                "    public int getAint() {\n" +
                "        return aint;\n" +
                "    }\n" +
                "\n" +
                "    public void setAint(int aint) {\n" +
                "        this.aint = aint;\n" +
                "    }\n" +
                "\n" +
                "    public Date getADate() {\n" +
                "        return aDate;\n" +
                "    }\n" +
                "\n" +
                "    public void setADate(Date aDate) {\n" +
                "        this.aDate = aDate;\n" +
                "    }\n" +
                "}";
    }
}

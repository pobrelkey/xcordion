package org.xcordion.ide.intellij;

import java.util.Date;


public class MyFirstXcordionSpecTestClass extends Object{
    String aString;
    Long aLong;
    int aint;
    Date aDate;

    public MyFirstXcordionSpecTestClass(String aString, Long aLong, int aint, Date aDate) {
        this.aString = aString;
        this.aLong = aLong;
        this.aint = aint;
        this.aDate = aDate;
    }

    public MyFirstXcordionSpecTestClass withSomething(){
        return this;
    }

    public MyFirstXcordionSpecTestClass withSomethingElse(){
        return this;
    }

    public Long build(){
        return new Long(123L);
    }

    public String getAString() {
        return aString;
    }

    public void setAString(String aString) {
        this.aString = aString;
    }

    public Long getALong() {
        return aLong;
    }

    public void setALong(Long aLong) {
        this.aLong = aLong;
    }

    public int getAint() {
        return aint;
    }

    public void setAint(int aint) {
        this.aint = aint;
    }

    public Date getADate() {
        return aDate;
    }

    public void setADate(Date aDate) {
        this.aDate = aDate;
    }
}

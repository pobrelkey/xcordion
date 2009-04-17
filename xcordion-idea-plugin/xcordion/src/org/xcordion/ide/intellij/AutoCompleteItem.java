package org.xcordion.ide.intellij;

public class AutoCompleteItem {
    private String text;
    private String type;

    public AutoCompleteItem(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

}

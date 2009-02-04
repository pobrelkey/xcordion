package org.xcordion.ide.intellij.settings;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JTextField testName;

    public SettingsPanel() {
        setLayout(new BorderLayout());
        Box box = Box.createVerticalBox();
        box.add(createTestClassNamePanel());
        box.add(Box.createVerticalStrut(10));
        add(box, BorderLayout.CENTER);
    }

    private Component createTestClassNamePanel() {
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBorder(BorderFactory.createTitledBorder("Fully Qualified Xcordion Test Class Name"));
        testName = new JTextField(50);
        namePanel.add(createTextFieldWithInstructions(testName, "All Xcordion test classes in this module will extend this class"));
        return namePanel;
    }

    private Component createTextFieldWithInstructions(JTextField widget, String instructions) {
        Box nameWidget = createLeftAlignedBox(widget);
        Box instructionsBox = createLeftAlignedBox(new JLabel(instructions));
        Box box = Box.createVerticalBox();
        box.add(nameWidget);
        box.add(instructionsBox);
        return box;
    }

    private Box createLeftAlignedBox(JComponent comp) {
        Box box = Box.createHorizontalBox();
        box.add(comp);
        box.add(Box.createHorizontalGlue());
        return box;
    }

    public String getXcordionBackingClassName() {
        return testName.getText();
    }

    public void setXcordionBackingClassName(String xcordionBackingClassName) {
        this.testName.setText(xcordionBackingClassName);
    }
}

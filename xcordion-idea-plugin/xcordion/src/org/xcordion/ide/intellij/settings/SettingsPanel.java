package org.xcordion.ide.intellij.settings;

import static javax.swing.BorderFactory.createTitledBorder;
import javax.swing.*;
import java.awt.*;

class SettingsPanel extends JPanel {
    private JTextField backingClassName;
    private final JCheckBox showConfirmationMessage = new JCheckBox("Prompt to create missing test classes");

    public SettingsPanel(XcordionSettings settings) {
        initialize();
        setSettings(settings);
    }

    private void initialize() {
        JPanel outer = new JPanel();
        outer.setLayout(new GridBagLayout());
        outer.add(createPanels(), createConstraints());
        add(outer, BorderLayout.CENTER);
    }

    private GridBagConstraints createConstraints() {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridwidth = GridBagConstraints.REMAINDER;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.gridx = 0;
        constraint.gridy = 0;
        return constraint;
    }

    private JPanel createPanels() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(createShowConfirmationMessagePanel());
        panel.add(createTestClassNamePanel());
        return panel;
    }

    private Component createShowConfirmationMessagePanel() {
        JPanel behaviourPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        behaviourPanel.setBorder(createTitledBorder("Behaviour"));
        behaviourPanel.add(showConfirmationMessage);
        return behaviourPanel;
    }

    //TODO:  Use a dialog to select the backing class from the classpath
    private Component createTestClassNamePanel() {
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBorder(createTitledBorder("Fully Qualified Xcordion Test Class Name"));
        backingClassName = new JTextField(50);
        namePanel.add(createClassNameTextField(backingClassName));
        return namePanel;
    }

    private Component createClassNameTextField(JTextField widget) {
        Box nameWidget = createLeftAlignedBox(widget);
        Box instructionsBox = createLeftAlignedBox(new JLabel("All Xcordion test classes in this module will extend this class"));
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

    public XcordionSettings getSettings() {
        XcordionSettings settings = new XcordionSettings();
        settings.setShowConfirmationMessage(showConfirmationMessage.isSelected());
        settings.setXcordionBackingClassName(backingClassName.getText());
        return settings;
    }

    public void setSettings(XcordionSettings settings) {
        if (settings != null) {
            this.showConfirmationMessage.setSelected(settings.showConfirmationMessage());
            this.backingClassName.setText(settings.getXcordionBackingClassName());
        }
    }
}

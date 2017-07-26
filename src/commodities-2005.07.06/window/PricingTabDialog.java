/*  x Properly Documented
 */
package commodities.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import commodities.price.*;

/**
 *  The PricingTabDialog class creates a dialog box with a
 *  form for creating a new tab and a starting commodity.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class PricingTabDialog extends JDialog {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The Commit button. */
    private JButton acceptBtn = new JButton("Commit");
    /** The Cancel button. */
    private JButton cancelBtn = new JButton("Cancel");

    /** The name of the tab text field. */
    private JTextField  tabNameTF   = new JTextField(15);
    /** The commodity selection combo box */
    private JComboBox   commodityCB;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  The FieldPanel class creates a panel that contains the name
     *  of a field and a text box for that field.
     *
     *  @author J.R. Titko
     */
    private class FieldPanel extends JPanel {
        /**
         *  Create a panel with a label (title) and text field for input.
         *
         *  @param  textField   The text field to be edited
         *  @param  title       The label to be displayed for the text field
         */
        public FieldPanel(JComponent component, String title) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(180, 45));
            JLabel label = new JLabel(title);
            label.setLabelFor(component);
            add(label);
            add(component);
        }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a dialog box for a new tab to be created.
     */
    public PricingTabDialog(Object choices[]) {
        super(CommodityAnalyzerJFrame.instance(), "Add Pricing Tab", true);

        commodityCB = new JComboBox(choices);
        setupGUI();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Set up the GUI interface of the new tab dialog box.
     */
    private void setupGUI() {
        setResizable(false);

        acceptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!"".equals(tabNameTF.getText()) &&
                        commodityCB.getSelectedItem() != null) {

                        if (PricingTabbedPane.instance().addTabToPanel(tabNameTF.getText())) {
                            PricingTabbedPane.instance().addToCurrentPricingPanel((String)commodityCB.getSelectedItem());
                            setVisible(false);
                            dispose();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "Fields are not propertly formatted.");
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        getRootPane().setDefaultButton(acceptBtn);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setPreferredSize(new Dimension(250, 100));


        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setPreferredSize(new Dimension(180, 40));
        JLabel label = new JLabel("Tab Name (ex: Metals)");
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setLabelFor(tabNameTF);
        fieldPanel.add(label);
        fieldPanel.add(tabNameTF);
        inputPanel.add(fieldPanel);


        fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setPreferredSize(new Dimension(180, 40));
        label = new JLabel("Commodity to display");
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setLabelFor(commodityCB);
        fieldPanel.add(label);
        fieldPanel.add(commodityCB);
        inputPanel.add(fieldPanel);



        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(acceptBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(Box.createHorizontalGlue());

        Container contentPane = getContentPane();
        contentPane.add(inputPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);

    }

}
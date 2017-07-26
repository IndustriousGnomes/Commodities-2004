/*  _ Properly Documented
 */
/**
 *  The PricingTabDialog class creates a dialog box with a
 *  form for creating a new tab and a starting commodity.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import prototype.commodities.dataaccess.*;
import prototype.commodities.*;
import prototype.commodities.price.*;

public class PricingTabDialog extends JDialog {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    private PricingTabDialog myDialog;

    private JButton acceptBtn = new JButton("Commit");
    private JButton cancelBtn = new JButton("Cancel");

    private JTextField  tabNameTF   = new JTextField(15);
private JTextField  tabNameTF2   = new JTextField(15);
    private JComboBox   commodityCB;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    private class FieldPanel extends JPanel {
        JLabel label;
        public FieldPanel(JComponent component, String title) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(180, 45));
            label = new JLabel(title);
            label.setLabelFor(component);
            add(label);
            add(component);
        }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public PricingTabDialog(Object choices[]) {
        super(CommodityJFrame.frame, "Add Pricing Tab", true);

        myDialog = this;
        commodityCB = new JComboBox(choices);
        setupGUI();
    }


/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    private void setupGUI() {
        setResizable(false);

        acceptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!"".equals(tabNameTF.getText()) &&
                        commodityCB.getSelectedItem() != null) {

                        if (PricingTabbedPane.getInstance().addTabToPanel(tabNameTF.getText(), (String)commodityCB.getSelectedItem())) {
                            myDialog.setVisible(false);
                            myDialog.dispose();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CommodityJFrame.frame, "Fields are not propertly formatted.");
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myDialog.setVisible(false);
                myDialog.dispose();
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
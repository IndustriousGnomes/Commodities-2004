/*  x Properly Documented
 */
package commodities.window;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import commodities.commodity.*;
import commodities.contract.*;
import commodities.dataaccess.*;
import commodities.util.*;

/**
 *  The ContractDialog class creates a dialog box with a
 *  form for editing contract informaiton.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class ContractDialog extends JDialog {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();
//    private ContractDialog myDialog;
//    private Commodity commodity = null;
    /** The contract to be editted. */
    private Contract contract = null;

    /** The Commit button. */
    private JButton acceptBtn = new JButton("Commit");
    /** The Cancel button. */
    private JButton cancelBtn = new JButton("Cancel");

    /** Commodity id text field. */
    private JTextField commodityIdTF    = new JTextField(15);
    /** Contract month text field. */
    private JTextField contractMonthTF  = new JTextField(15);
    /** Price text field. */
    private JTextField marginTF          = new JTextField(15);

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  The FieldPanel class creates a panel that contains the name
     *  of a field and a text box for that field.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.11
     */
    private class FieldPanel extends JPanel {
        JLabel label;
        /**
         *  Create a panel with a label (title) and text field for input.
         *
         *  @param  textField   The text field to be edited
         *  @param  title       The label to be displayed for the text field
         */
        public FieldPanel(JTextField textField, String title) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(180, 35));
            label = new JLabel(title);
            label.setLabelFor(textField);
            add(label);
            add(textField);
        }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
//    public ContractDialog(Commodity commodity, Contract contract) {
    public ContractDialog(Contract contract) {
        super(CommodityAnalyzerJFrame.instance(), "Contract Maintenance", true);

//        myDialog = this;
//        this.commodity = commodity;
        this.contract = contract;
        setupGUI();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    private void setupGUI() {
        setResizable(false);

        acceptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    contract.setMargin(Integer.parseInt(marginTF.getText()));
                    try {
                        dataManager.updateContract(contract);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    setVisible(false);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "Fields are not propertly formatted.");
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
                dispose();
            }
        });

        getRootPane().setDefaultButton(acceptBtn);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setPreferredSize(new Dimension(200, 180));

        commodityIdTF.setText(Commodity.bySymbol(contract.getSymbol()).getNameExchange());
        commodityIdTF.setEditable(false);
        contractMonthTF.setText(contract.getMonth());
        contractMonthTF.setEditable(false);

        marginTF.setText(""+contract.getMargin());

        inputPanel.add(new FieldPanel(commodityIdTF, "Commodity"));
        inputPanel.add(new FieldPanel(contractMonthTF, "Contract"));
        inputPanel.add(new FieldPanel(marginTF, "Margin (ex: 1500)"));


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
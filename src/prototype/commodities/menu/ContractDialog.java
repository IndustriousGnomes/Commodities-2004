/*  _ Properly Documented
 */
/**
 *  The ContractDialog class creates a dialog box with a
 *  form for editing contract informaiton.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import prototype.commodities.dataaccess.*;
import prototype.commodities.*;

public class ContractDialog extends JDialog {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    private ContractDialog myDialog;
//    private Commodity commodity = null;
    private Contract contract = null;

    private JButton acceptBtn = new JButton("Commit");
    private JButton cancelBtn = new JButton("Cancel");

    private JTextField commodityIdTF    = new JTextField(15);
    private JTextField contractMonthTF  = new JTextField(15);
    private JTextField priceTF          = new JTextField(15);


/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    private class FieldPanel extends JPanel {
        JLabel label;
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
        super(CommodityJFrame.frame, "Contract Maintenance", true);

        myDialog = this;
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
            public void actionPerformed(ActionEvent e) {
                try {
                    Contract cont = new Contract(contract.getId(),
                                                 contract.getSymbol(),
                                                 contract.getMonth(),
                                                 Integer.parseInt(priceTF.getText()));

                    Contract.editContract(cont);

                    myDialog.setVisible(false);
                    myDialog.dispose();
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
        inputPanel.setPreferredSize(new Dimension(200, 180));

        commodityIdTF.setText(Commodity.bySymbol(contract.getSymbol()).getNameExchange());
        commodityIdTF.setEditable(false);
        contractMonthTF.setText(contract.getMonth());
        contractMonthTF.setEditable(false);

        priceTF.setText(""+contract.getPrice());

        inputPanel.add(new FieldPanel(commodityIdTF, "Commodity"));
        inputPanel.add(new FieldPanel(contractMonthTF, "Contract"));
        inputPanel.add(new FieldPanel(priceTF, "Price (ex: 1500)"));


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
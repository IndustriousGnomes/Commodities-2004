/* _ Review Javadocs */
package commodities.window;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import commodities.account.*;
import commodities.dataaccess.*;

/**
 *  The AccountDialog class creates a dialog box with a form
 *  for editing account information.
 *
 *  @author     J.R. Titko
 *  @since      1.00
 *  @version 1.0
 *  @update 2004.11.11
 */

public class AccountDialog extends JDialog {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();
    /** Dialog width */
    private static final int DIALOG_WIDTH = 400;
    /** Dialog height */
    private static final int DIALOG_HEIGHT = 160;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The account to be edited. */
    private Account account;

    /** The Commit button. */
    private JButton acceptBtn = new JButton("Commit");
    /** The Cancel button. */
    private JButton cancelBtn = new JButton("Cancel");

    /** The first name of the account owner. */
    private JTextField firstNameTF      = new JTextField(15);
    /** The last name of the account owner. */
    private JTextField lastNameTF       = new JTextField(15);
    /** The capital in the account. */
    private JTextField capitalTF        = new JTextField(15);
    /** The current value of the account. */
    private JTextField acctValueTF      = new JTextField(15);


/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  The FieldPanel class creates a panel that contains the name
     *  of a field and a text box for that field.
     *
     *  @author J.R. Titko
     *  @since      1.00
     *  @version 1.0
     *  @update 2004.11.11
     */

    private class FieldPanel extends JPanel {
        /**
         *  Create a panel with a label (title) and text field for input.
         *
         *  @param  textField   The text field to be edited
         *  @param  title       The label to be displayed for the text field
         */
        public FieldPanel(JTextField textField, String title) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(180, 35));
            JLabel label = new JLabel(title);
            label.setLabelFor(textField);
            add(label);
            add(textField);
        }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a dialog box for a new account to be entered.
     */
    public AccountDialog() {
        this(null);
    }

    /**
     *  Create a dialog box for an existing account to be edited.
     *
     *  @param  account   The account to be edited
     */
    public AccountDialog(Account account) {
        super(CommodityAnalyzerJFrame.instance(), "Account Maintenance", true);

        this.account = account;
        setupGUI();
    }

/* *************************************** */
/* *** <Extended ClassName> METHODS    *** */
/* *************************************** */

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */


/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Setup the GUI interface of the account dialog box.
     */
    private void setupGUI() {
        setResizable(false);
        Dimension screenSize = CommodityAnalyzerJFrame.instance().getBounds().getSize();
        setLocation(((screenSize.width - DIALOG_WIDTH) / 2), ((screenSize.height - DIALOG_HEIGHT) / 3));

        acceptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    if (!"".equals(firstNameTF.getText()) &&
                        !"".equals(lastNameTF.getText()) &&
                        !"".equals(capitalTF.getText())) {

                        if (account == null) {
                            account = new Account(firstNameTF.getText(),
                                                  lastNameTF.getText(),
                                                  Double.parseDouble(capitalTF.getText()));
                            try {
                                dataManager.addAccount(account);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                       } else {
                            account.setFirstName(firstNameTF.getText());
                            account.setLastName(lastNameTF.getText());
                            account.setCapital(Double.parseDouble(capitalTF.getText()));
                            try {
                                dataManager.updateAccount(account);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        setVisible(false);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "Not all required fields are entered.");
                    }
                } catch (NumberFormatException e) {
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
        inputPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));

        if (account != null) {
            firstNameTF.setText(account.getFirstName());
            lastNameTF.setText(account.getLastName());
            capitalTF.setText("" + account.getCapital());
            acctValueTF.setText("" + account.getAccountValue());
        }
        acctValueTF.setEditable(false);

        inputPanel.add(new FieldPanel(firstNameTF, "First Name"));
        inputPanel.add(new FieldPanel(lastNameTF, "Last Name*"));
        inputPanel.add(new FieldPanel(capitalTF, "Capital*"));
        inputPanel.add(new FieldPanel(acctValueTF, "Account Value"));

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
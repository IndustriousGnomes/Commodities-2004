/*  x Properly Documented
 */
package commodities.window;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import commodities.dataaccess.*;
import commodities.commodity.*;
import commodities.window.*;

/**
 *  The CommodityDialog class creates a dialog box with a
 *  form for editing commodity informaiton.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class CommodityDialog extends JDialog {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();
    /** Dialog width */
    private static final int DIALOG_WIDTH = 600;
    /** Dialog height */
    private static final int DIALOG_HEIGHT = 220;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The commodity to be edited. */
    private Commodity commodity = null;

    /** The Commit button. */
    private JButton acceptBtn = new JButton("Commit");
    /** The Cancel button. */
    private JButton cancelBtn = new JButton("Cancel");

    /** The commodity symbol text field. */
    private JTextField symbolTF             = new JTextField(15);
    /** The commodity exchange text field. */
    private JTextField exchangeTF           = new JTextField(15);
    /** The english name of the commodity text field. */
    private JTextField nameTF               = new JTextField(15);
    /** The commodity's unit of trade text field. */
    private JTextField unitTypeTF           = new JTextField(15);
    /** The number of units in the contract text field. */
    private JTextField unitSizeTF           = new JTextField(15);
    /** The type of price the unit is priced in (dollars, cents, etc) text field. */
    private JTextField unitPriceTF          = new JTextField(15);
    /** The size of a tick (minimum movement size) text field. */
    private JTextField tickSizeTF           = new JTextField(15);
    /** The price of a tick (cost of a single tick move) text field. */
    private JTextField tickPriceTF          = new JTextField(15);
    /** The maximum number of ticks a price can move in a day text field. */
    private JTextField tickDailyLimitTF     = new JTextField(15);
    /** The standard price of a commodity contract text field. */
    private JTextField marginTF      = new JTextField(15);
    /** The number of decimals a price is displayed with text field. */
    private JTextField decimalsTF           = new JTextField(15);
    /** The trading month symbols for the commodity text field. */
    private JTextField monthsTF           = new JTextField(15);
    /** The open outcry symbol text field. */
    private JTextField openOutcrySymbolTF   = new JTextField(15);
    /** The a/c/m symbol text field. */
    private JTextField acmSymbolTF          = new JTextField(15);
    /** The special DB symbol text field. */
    private JTextField databaseSymbolTF     = new JTextField(15);


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
     *  Create a dialog box for a new commodity to be entered.
     */
    public CommodityDialog() {
        this(null);
    }

    /**
     *  Create a dialog box for an existing commodity to be edited.
     *
     *  @param  commodity   The commodity to be edited
     */
    public CommodityDialog(Commodity commodity) {
        super(CommodityAnalyzerJFrame.instance(), "Commodity Maintenance", true);

        this.commodity = commodity;
        setupGUI();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Set up the GUI interface of the commodity dialog box.
     */
    private void setupGUI() {
        setResizable(false);
        Dimension screenSize = CommodityAnalyzerJFrame.instance().getBounds().getSize();
        setLocation(((screenSize.width - DIALOG_WIDTH) / 2), ((screenSize.height - DIALOG_HEIGHT) / 3));

        acceptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    if (!"".equals(symbolTF.getText()) &&
                        !"".equals(exchangeTF.getText()) &&
                        !"".equals(nameTF.getText()) &&
                        !"".equals(unitTypeTF.getText()) &&
                        !"".equals(unitSizeTF.getText()) &&
                        !"".equals(unitPriceTF.getText()) &&
                        !"".equals(tickSizeTF.getText()) &&
                        !"".equals(tickPriceTF.getText()) &&
                        !"".equals(tickDailyLimitTF.getText()) &&
                        !"".equals(marginTF.getText()) &&
                        !"".equals(decimalsTF.getText()) &&
                        !"".equals(monthsTF.getText())) {

                        int monthMask = formatMonthMask(monthsTF.getText());

                        if (commodity == null) {
                            commodity = new Commodity(symbolTF.getText(),
                                                      exchangeTF.getText(),
                                                      nameTF.getText(),
                                                      unitTypeTF.getText(),
                                                      Integer.parseInt(unitSizeTF.getText()),
                                                      unitPriceTF.getText(),
                                                      Double.parseDouble(tickSizeTF.getText()),
                                                      Double.parseDouble(tickPriceTF.getText()),
                                                      Integer.parseInt(tickDailyLimitTF.getText()),
                                                      Integer.parseInt(marginTF.getText()),
                                                      Integer.parseInt(decimalsTF.getText()),
                                                      openOutcrySymbolTF.getText(),
                                                      acmSymbolTF.getText(),
                                                      monthMask,
                                                      databaseSymbolTF.getText());
                            try {
                                dataManager.addCommodity(commodity);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            commodity.setName(nameTF.getText());
                            commodity.setUnitType(unitTypeTF.getText());
                            commodity.setUnitSize(Integer.parseInt(unitSizeTF.getText()));
                            commodity.setUnitPrice(unitPriceTF.getText());
                            commodity.setTickSize(Double.parseDouble(tickSizeTF.getText()));
                            commodity.setTickPrice(Double.parseDouble(tickPriceTF.getText()));
                            commodity.setTickDailyLimit(Integer.parseInt(tickDailyLimitTF.getText()));
                            commodity.setMargin(Integer.parseInt(marginTF.getText()));
                            commodity.setDisplayDecimals(Integer.parseInt(decimalsTF.getText()));
                            commodity.setOpenOutcrySymbol(openOutcrySymbolTF.getText());
                            commodity.setAcmSymbol(acmSymbolTF.getText());
                            commodity.setTradeMonthMask(monthMask);
                            commodity.setDatabaseSymbol(databaseSymbolTF.getText());

                            try {
                                dataManager.updateCommodity(commodity);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        setVisible(false);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "Not all required fields are entered.");
                    }
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
        inputPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));

        if (commodity != null) {
            symbolTF.setText(commodity.getSymbol());
            symbolTF.setEditable(false);
            exchangeTF.setText(commodity.getExchange());
            exchangeTF.setEditable(false);

            nameTF.setText(commodity.getName());
            unitTypeTF.setText(commodity.getUnitType());
            unitSizeTF.setText(""+commodity.getUnitSize());
            unitPriceTF.setText(commodity.getUnitPrice());
            tickSizeTF.setText(""+commodity.getTickSize());
            tickPriceTF.setText(""+commodity.getTickPrice());
            tickDailyLimitTF.setText(""+commodity.getTickDailyLimit());
            marginTF.setText(""+commodity.getMargin());
            decimalsTF.setText(""+commodity.getDisplayDecimals());
            monthsTF.setText(formatMonths(commodity.getTradeMonthMask()));
            openOutcrySymbolTF.setText(commodity.getOpenOutcrySymbol());
            acmSymbolTF.setText(commodity.getAcmSymbol());
            databaseSymbolTF.setText(commodity.getDatabaseSymbol());
        }

        inputPanel.add(new FieldPanel(symbolTF, "Symbol (ex: S)"));
        inputPanel.add(new FieldPanel(exchangeTF, "Exchange (ex: CBOT)"));
        inputPanel.add(new FieldPanel(nameTF, "Name (ex: Soybeans)"));
        inputPanel.add(new FieldPanel(unitTypeTF, "Unit Type  (ex: bu)"));
        inputPanel.add(new FieldPanel(unitSizeTF, "Unit Size (ex: 5000)"));
        inputPanel.add(new FieldPanel(unitPriceTF, "Unit Price  (cents)"));
        inputPanel.add(new FieldPanel(tickSizeTF, "Tick Size (ex: 0.25)"));
        inputPanel.add(new FieldPanel(tickPriceTF, "Tick Price (ex: 12.50)"));
        inputPanel.add(new FieldPanel(tickDailyLimitTF, "Tick Daily Limit (ex: 200)"));
        inputPanel.add(new FieldPanel(marginTF, "Margin (ex: 1500)"));
        inputPanel.add(new FieldPanel(decimalsTF, "Decimals to Display (ex: 2)"));
        inputPanel.add(new FieldPanel(monthsTF, "Months Traded (ex: JAN, FEB)"));
        inputPanel.add(new FieldPanel(openOutcrySymbolTF, "Open Outcry Symbol (opt)"));
        inputPanel.add(new FieldPanel(acmSymbolTF, "ACM Symbol (opt)"));
        inputPanel.add(new FieldPanel(databaseSymbolTF, "Database Symbol (opt)"));


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

    /**
     *  Format the names of the months based on the month mask for the commodity.
     *  The names are formatted using a comma dilimited list of 3 char month names.
     *
     *  @param  monthMask   The binary month mask for the commodity trading months
     *  @return             The list of trading months in string format
     */
    private String formatMonths(int monthMask) {
        String months = "";
        boolean first = true;
        if (!"".equals(months += formatMonth(monthMask, Commodity.JAN, "JAN", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.FEB, "FEB", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.MAR, "MAR", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.APR, "APR", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.MAY, "MAY", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.JUN, "JUN", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.JUL, "JUL", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.AUG, "AUG", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.SEP, "SEP", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.OCT, "OCT", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.NOV, "NOV", first))) { first = false; };
        if (!"".equals(months += formatMonth(monthMask, Commodity.DEC, "DEC", first))) { first = false; };
        return months;
    }

    /**
     *  Perform the formatting logic for a single month.
     *
     *  @param  monthMask   The binary month mask for the commodity trading months
     *  @param  mask        The binary month mask for a single month
     *  @param  month       The month string to be used if found in the month mask
     *  @param  first       Is this the first month found for the month string
     *  @return             The list of trading months in string format
     */
    private String formatMonth(int monthMask, int mask, String month, boolean first) {
        String label = "";
        if ((monthMask & mask) > 0) {
            label = first?month:", " + month;
        }
        return label;
    }

    /**
     *  Format the month mask based on the 3 character month names.
     *
     *  @param  monthMask   The list of trading months in string format
     *  @return             The binary month mask for the commodity trading months
     */
    private int formatMonthMask(String monthText) {
        int monthMask = 0;
        monthText = monthText.toUpperCase();

        if (monthText.indexOf("JAN") > -1) { monthMask += Commodity.JAN; }
        if (monthText.indexOf("FEB") > -1) { monthMask += Commodity.FEB; }
        if (monthText.indexOf("MAR") > -1) { monthMask += Commodity.MAR; }
        if (monthText.indexOf("APR") > -1) { monthMask += Commodity.APR; }
        if (monthText.indexOf("MAY") > -1) { monthMask += Commodity.MAY; }
        if (monthText.indexOf("JUN") > -1) { monthMask += Commodity.JUN; }
        if (monthText.indexOf("JUL") > -1) { monthMask += Commodity.JUL; }
        if (monthText.indexOf("AUG") > -1) { monthMask += Commodity.AUG; }
        if (monthText.indexOf("SEP") > -1) { monthMask += Commodity.SEP; }
        if (monthText.indexOf("OCT") > -1) { monthMask += Commodity.OCT; }
        if (monthText.indexOf("NOV") > -1) { monthMask += Commodity.NOV; }
        if (monthText.indexOf("DEC") > -1) { monthMask += Commodity.DEC; }
        return monthMask;
    }
}

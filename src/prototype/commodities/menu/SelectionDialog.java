/*  _ Properly Documented
 */
/**
 *  The SelectionDialog class creates a dialog box containing
 *  selections in the form of checkboxes.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import prototype.commodities.*; // debug only

public class SelectionDialog extends JDialog {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
//    private Frame frame;
    private JList list;
    private int selectionMode;
    private Object choices[];
    private Object selected[];
    private SelectionDialog myDialog;

    private JButton acceptBtn;
    private JButton cancelBtn = new JButton("Cancel");

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public SelectionDialog(Frame frame, String title, Object choices[], String acceptButtonName, int selectionMode) {
        super(frame, title, true);
//        this.frame = frame;
        this.choices = choices;
        this.selectionMode = selectionMode;
        acceptBtn = new JButton(acceptButtonName);

        myDialog = this;
        setupGUI();
    }

    public SelectionDialog(Frame frame, String title, Object choices[], int selectionMode) {
        this(frame, title, choices, "Ok", selectionMode);
    }

    public SelectionDialog(Frame frame, String title, Object choices[], String acceptButtonName) {
        this(frame, title, choices, "Ok", ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public SelectionDialog(Frame frame, String title, Object choices[]) {
        this(frame, title, choices, "Ok", ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    private void setupGUI() {
        acceptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selected = list.getSelectedValues();
                myDialog.setVisible(false);
                myDialog.dispose();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myDialog.setVisible(false);
                myDialog.dispose();
            }
        });

        getRootPane().setDefaultButton(acceptBtn);

        list = new JList(choices);
        list.setSelectionMode(selectionMode);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    acceptBtn.doClick();
                }
            }
        });

        JScrollPane jsp = new JScrollPane(list);
        jsp.setPreferredSize(new Dimension(250, 80));
        jsp.setMinimumSize(new Dimension(250, 80));

        JPanel listPanel = new JPanel();
        listPanel.add(jsp);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10,5,5,5));


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(acceptBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(Box.createHorizontalGlue());

        Container contentPane = getContentPane();
        contentPane.add(listPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

//        setLocationRelativeTo(frame);
        pack();
        setVisible(true);

    }

    public Object[] getSelected() {
        return selected;
    }


}
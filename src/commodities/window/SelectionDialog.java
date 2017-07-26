/*  x Properly Documented
 */
package commodities.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  The SelectionDialog class creates a dialog box containing
 *  selections in the form of a list.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class SelectionDialog extends JDialog {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
//    private Frame frame;
    /** List of items to be selected from in the dialog box. */
    private JList list;
    /** The selection mode of the list: single or multiple selection.  */
    private int selectionMode;
    /** The list of choices passed in as an array. */
    private Object choices[];
    /** The list of selected entities from the list as an array. */
    private Object selected[];
    /** The current instance */
//    private SelectionDialog myDialog;

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

//        myDialog = this;
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
//                myDialog.setVisible(false);
                setVisible(false);
//                myDialog.dispose();
                dispose();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                myDialog.setVisible(false);
//                myDialog.dispose();
                setVisible(false);
                dispose();
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
/*  x Properly Documented
 */
package prototype.commodities.price;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;

import prototype.commodities.*;
import prototype.commodities.graph.*;
import prototype.commodities.tests.*;
import prototype.commodities.dataaccess.*;

/**
 *  The PricePanel displays all of the pricing information for a given commodity and its
 *  contracts in a table format.
 *
 *  @author J.R. Titko
 */
public class PricePanel extends JPanel {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The preferred width of the price panel. */
    private static final int PREFERRED_PANEL_WIDTH   = 500;
    /** The preferred height of the price panel. */
    private static final int PREFERRED_PANEL_HEIGHT  = 148;
    /** Width of the price panel with the border included */
    protected static final int PANEL_WIDTH   = PREFERRED_PANEL_WIDTH + 2;
    /** Height of the price panel with the border and title included */
    protected static final int PANEL_HEIGHT  = PREFERRED_PANEL_HEIGHT + 19;

    /* TABLE CELLS */
    /** Contract column */
    private static final int CONTRACT_COLUMN = 0;
    /** Short term recommendation column */
    private static final int SHORT_TERM_COLUMN = 7;
    /** Medium term recommendation column */
    private static final int MEDIUM_TERM_COLUMN = 8;
    /** Long term recommendation column */
    private static final int LONG_TERM_COLUMN = 9;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The JTable that will be used to display the data. */
    private JTable table;
    /** The table model for formatting the JTable. */
    private PriceTableModel tableModel;
    /** The panel in which this panel will be presented. */
    private PricingPanel pricingPanel;
    /** The JScrollPane to allow the JTable to be scrolled. */
    private JScrollPane jsp;
    /** The commodity to be presented in the JTable. */
    private Commodity commodity;
    /** A reference to this PricePanel. */
    private PricePanel myPricePanel;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  The table model for laying out the JTable's appearance
     *  for displaying a commodity's information.
     *
     *  @author J.R. Titko
     */
    public class PriceTableModel extends AbstractTableModel {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The names of the columns of the JTable. */
        private String columnNames[] = {"Contract", "Open", "High", "Low", "Close", "Interest", "Volume", "RS", "RM", "RL"};
        /** The rows of data stored in the JTable. */
        private ArrayList rowData = new ArrayList();

    /* *************************************** */
    /* *** INSTANCE METHODS                *** */
    /* *************************************** */
        /**
         *  Adds a new row to the bottom of the JTable containing the appropriate
         *  information retrieved from the passed in fields.
         *
         *  @param  commodity       the commodity the information is for
         *  @param  contract        the specific contract to add
         *  @param  recommendation  the buy/sell recommendation
         */
        public void addRow(Commodity commodity, Contract contract) {
            Prices prices = contract.getPrices();
            Object columnData[] = {contract.getMonthFormatted(),
                                   commodity.formatPrice(prices.getOpen()),
                                   commodity.formatPrice(prices.getHigh()),
                                   commodity.formatPrice(prices.getLow()),
                                   commodity.formatPrice(prices.getClose()),
                                   prices.getInterestObject(),
                                   prices.getVolumeObject(),
                                   "",
                                   "",
                                   ""};
            rowData.add(columnData);
            fireTableRowsInserted(rowData.size(),rowData.size());

            (TestManager.instance()).getRecommendation(contract, TechnicalTestInterface.SHORT_TERM, this, rowData.size());
            (TestManager.instance()).getRecommendation(contract, TechnicalTestInterface.MEDIUM_TERM, this, rowData.size());
            (TestManager.instance()).getRecommendation(contract, TechnicalTestInterface.LONG_TERM, this, rowData.size());
        }

        /**
         *  Set the short term recommendation for the commodity row.
         *  @param  row             the row to update
         *  @param  recommendation  the recommendation to put in the cell
         */
        public void setShortTermRecommendation(int row, Recommendation recommendation) {
            Object columnData[] = (Object[])rowData.get(row - 1);
            columnData[SHORT_TERM_COLUMN] = recommendation.getRecommendationLabel();
            fireTableDataChanged();
        }

        /**
         *  Set the medium term recommendation for the commodity row.
         *  @param  row             the row to update
         *  @param  recommendation  the recommendation to put in the cell
         */
        public void setMediumTermRecommendation(int row, Recommendation recommendation) {
            Object columnData[] = (Object[])rowData.get(row - 1);
            columnData[MEDIUM_TERM_COLUMN] = recommendation.getRecommendationLabel();
            fireTableDataChanged();
        }

        /**
         *  Set the long term recommendation for the commodity row.
         *  @param  row             the row to update
         *  @param  recommendation  the recommendation to put in the cell
         */
        public void setLongTermRecommendation(int row, Recommendation recommendation) {
            Object columnData[] = (Object[])rowData.get(row - 1);
            columnData[LONG_TERM_COLUMN] = recommendation.getRecommendationLabel();
            fireTableDataChanged();
        }

    /* *************************************** */
    /* *** AbstractTableModel METHODS      *** */
    /* *************************************** */
        /**
         *  Gets the number of columns in the JTable.
         *  @return         number of columns
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         *  Gets the number of rows in the JTable.
         *  @return         number of rows
         */
        public int getRowCount() {
            return rowData.size();
        }

        /**
         *  Gets the name of the indexed column in the JTable.
         *
         *  @param  col     the column to get the name for
         *  @return         title of column
         */
        public String getColumnName(int col) {
            return columnNames[col];
        }

        /**
         *  Gets the value of the cell in the JTable by row and column.
         *
         *  @param  row     the row of the cell
         *  @param  col     the column of the cell
         *  @return         contents of the cell
         */
        public Object getValueAt(int row, int col) {
            Object columnData[] = (Object[])rowData.get(row);
            return columnData[col];
        }

// NOTE: NEEDED FOR SETTING BUY/SELL ORDERS
        /**
         *  Sets the value of the cell at row and column with the object in value.
         *
         *  @param  value   the object to put in the cell
         *  @param  row     the row of the cell
         *  @param  col     the column of the cell
         */
        public void setValueAt(Object value, int row, int col) {
            if (((Object[])rowData.get(0))[col] instanceof Integer && !(value instanceof Integer)) {
            } else {
                Object columnData[] = (Object[])rowData.get(row);
                columnData[col] = value;
                fireTableCellUpdated(row, col);
            }
        }
    }

    /**
     *  The cell renderer for laying out the appearence of individual
     *  JTable cells.
     *
     *  @author J.R. Titko
     */
    public class PriceTableCellRenderer extends DefaultTableCellRenderer {
    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /** Create a PriceTableCellRenderer. */
        public PriceTableCellRenderer() {
            setOpaque(true);
        }

    /* **************************************** */
    /* *** DefaultTableCellRenderer METHODS *** */
    /* **************************************** */
        /**
         *  Gets the rendering of a cell based on information contained
         *  in those cells.
         *
         *  @param  table       the JTable
         *  @param  value       the value to assign to the cell at [row, column]
         *  @param  isSelected  true if cell is selected
         *  @param  hasFocus    if true, render cell appropriately.
         *  @param  row         the row of the cell to render
         *  @param  column      the column of the cell to render
         *  @return             this component as a properly rendered cell
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == CONTRACT_COLUMN) {
                setHorizontalAlignment(LEFT);
            } else if ((column == SHORT_TERM_COLUMN) ||
                       (column == MEDIUM_TERM_COLUMN) ||
                       (column == LONG_TERM_COLUMN)) {
                setHorizontalAlignment(CENTER);
                setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
                if (value != null) {
                    String s = value.toString();
                    if (Recommendation.BUY_LABEL.equals(s) || Recommendation.SETTLE_BUY_LABEL.equals(s)) {
                        setBackground(Color.GREEN);
                        setForeground(Color.BLACK);
                    } else if (Recommendation.SELL_LABEL.equals(s) || Recommendation.SETTLE_SELL_LABEL.equals(s)) {
                        setBackground(Color.CYAN);
                        setForeground(Color.BLACK);
                    } else if (Recommendation.NO_ACTION_LABEL.equals(s)) {
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    } else {
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }

                }
            } else {
                setHorizontalAlignment(RIGHT);
            }

            if (isSelected || hasFocus) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else if ((row == CONTRACT_COLUMN) &&
                       ((column != SHORT_TERM_COLUMN) && (column != MEDIUM_TERM_COLUMN) && (column != LONG_TERM_COLUMN))) {
                setBackground(Color.RED);
                setForeground(Color.BLACK);
            }


            return this;
        }
    }



/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a new PricePanel for the commodity with a reference
     *  back to the PricingPanel that this panel will reside in.
     *
     *  @param  pricingPanel    a callback to the PricingPanel
     *  @param  commodity       the commodity to set up in this PricePanel
     */
    public PricePanel (PricingPanel pricingPanel, Commodity commodity) {
Debug.println(DEBUG, this, "PricePanel start");
        this.commodity = commodity;
        this.pricingPanel = pricingPanel;
        this.myPricePanel = this;

        setupGUI();

        Iterator it = commodity.getContracts();
        while (it.hasNext()) {
            Contract contract = (Contract)it.next();
            addRow(commodity, contract);
        }
Debug.println(DEBUG, this, "PricePanel end");
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Set up the layout of the PricePanel with a header and JTable.
     */
    private void setupGUI() {
Debug.println(DEBUG, this, "setupGUI start");
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));

        tableModel = new PriceTableModel();
        table = new JTable(tableModel) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new PriceTableCellRenderer();
            }
        };
        table.setSelectionBackground(Color.BLACK);
        table.setSelectionForeground(Color.WHITE);
        table.getColumn(tableModel.getColumnName(CONTRACT_COLUMN)).setPreferredWidth(125);
        table.getColumn(tableModel.getColumnName(SHORT_TERM_COLUMN)).setPreferredWidth(32);
        table.getColumn(tableModel.getColumnName(MEDIUM_TERM_COLUMN)).setPreferredWidth(32);
        table.getColumn(tableModel.getColumnName(LONG_TERM_COLUMN)).setPreferredWidth(32);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Color.BLUE);
        tableHeader.setForeground(Color.WHITE);



        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.YELLOW);

        JLabel contPrice = new JLabel("  Cost=$" + commodity.getStandardPrice());
        contPrice.setFont(new Font(contPrice.getFont().getName(), Font.PLAIN, contPrice.getFont().getSize()-2));

        JLabel title = new JLabel(commodity.getName() + " (" + commodity.getExchange() + ") " +
                                  commodity.getUnitSize() + " " + commodity.getUnitType() + "; " +
                                  commodity.getUnitPrice() + " per " + commodity.getUnitType());
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setForeground(Color.black);

        JLabel price = new JLabel("TS=" + commodity.getTickSize() + " ($" + commodity.getTickPrice() + ")  ");
        price.setFont(new Font(price.getFont().getName(), Font.PLAIN, price.getFont().getSize()-2));

        titlePanel.add(contPrice, BorderLayout.WEST);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(price, BorderLayout.EAST);

        jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setPreferredSize(new Dimension(PREFERRED_PANEL_WIDTH, PREFERRED_PANEL_HEIGHT));
        jsp.setViewportView(table);

        add(BorderLayout.NORTH, titlePanel);
        add(BorderLayout.CENTER, jsp);

        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {
                    //no rows are selected
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    String contractAbbrev = ((String)tableModel.getValueAt(selectedRow, 0)).trim();
                    String contractMonth = "20" + contractAbbrev.substring(4) + Month.byAbbrev(contractAbbrev.substring(0,3)).getSymbol();
                    PricingTabbedPane.getInstance().clearSelections(myPricePanel);

                    CommodityJFrame.graph.clearListeners();
                    new GraphDailyPrices(commodity.getContract(contractMonth), true);

                    TestManager.instance().removeAllGraphs();
                    PricingTabbedPane.getInstance().setSelectedContract(commodity.getContract(contractMonth));
                    prototype.commodities.menu.TestMenu.selectRecommendedMenuCheckboxes();
                }
            }
        });
Debug.println(DEBUG, this, "setupGUI end");
    }

    /**
     *  Adding a row to this panel actually adds a row to the
     *  JTable within the panel.
     *
     *  @param  commodity       the commodity the information is for
     *  @param  contract        the specific contract to add
     */
    public void addRow(Commodity commodity, Contract contract) {
Debug.println(DEBUG, this, "addRow start");
        tableModel.addRow(commodity, contract);
Debug.println(DEBUG, this, "addRow end");
    }

    /**
     *  Deselects all selected columns and rows in the JTable.
     */
    public void clearSelection() {
Debug.println(DEBUG, this, "clearSelection start");
        table.clearSelection();
Debug.println(DEBUG, this, "clearSelection end");
    }
/* *************************************** */
/* *** GETS & SETS                     *** */
/* *************************************** */
    /** Get the commodity in this panel. */
    public Commodity getCommodity() { return commodity; }

}

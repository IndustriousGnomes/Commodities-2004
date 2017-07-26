/*  x Properly Documented
 */
package commodities.dataaccess.dataloader;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Properties;

import commodities.commodity.*;
import commodities.contract.*;
import commodities.dataaccess.*;
import commodities.price.*;
import commodities.util.*;

import com.util.FormatDate;

/**
 *  The DailyDataLoaderCBOT processes the data files that originate from the
 *  Chicago Board of Trade (CBOT).
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class DailyDataLoaderCBOT {

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    protected   static  DataManagerInterface dataManager = DataManagerFactory.instance();
    /** The data from all of the pages to reorganize into files by date */
    private static TreeMap filedData = new TreeMap();

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
     *  Retrieve the daily CBOT price files.  If it is before 1pm, the current date will be
     *  set to the previous business date.
     */
    protected static void retrievePrices() {
        CommodityCalendar today = (CommodityCalendar)CommodityCalendar.getInstance();
        if (today.get(Calendar.HOUR_OF_DAY) < 13) {         // Is it before 1pm
            today.addWeekDays(-1);
            System.out.println("NOTICE: Retrieving prices for previous day.");
        }
        today.clearTime();
System.out.println("Today's date " + today.getTime());
        String dataFile = null;
        String todayDate    = "0" + today.get(Calendar.DATE);
        String todayMonth   = "0" + today.get(Calendar.MONTH);
        String subDirName = "CBOT04-" + (("" + today.get(Calendar.YEAR)).substring(2)) +
                                     (todayMonth.substring(todayMonth.length() - 2)) +
                                     (todayDate.substring(todayDate.length() - 2));

        File newDir = new File(DailyDataLoaderFactory.dataDir + "\\rawdata\\" + subDirName);
        newDir.mkdirs();

        Map commodityNameMap = Commodity.getNameMap();
        Iterator it = commodityNameMap.keySet().iterator();
        while (it.hasNext()) {
            Commodity commodity = Commodity.byNameExchange((String)it.next());
            String commoditySymbol = commodity.getSymbol();
            String databaseSymbol = commodity.getDatabaseCommoditySymbol();
            System.out.println("Retrieving commodity = " + commodity.getName() + "  DB Symbol = " + commodity.getDatabaseCommoditySymbol());

            int monthsMask = commodity.getTradeMonthMask();

            int monthMask = Commodity.JAN;
            int monthNumber = 0;
            while (monthMask > 0) {
                if ((monthsMask & monthMask) > 0) {
                    boolean currentMonth = (monthNumber == today.get(Calendar.MONTH))?true:false;
                    String  monthSymbol = Month.byNumber(monthNumber).getSymbol();
                    int     year = today.get(Calendar.YEAR);
                    Contract contract = null;

                    if (monthNumber >= today.get(Calendar.MONTH)) {
                        contract = commodity.getContract(year + monthSymbol);
                        if ((contract == null) || (!today.getTime().equals(contract.getCurrentDate()))) {
                            if (contract == null) {
                                System.out.print("~");
                            } else {
                                System.out.print(".");
                            }
                            dataFile = retrieveFile(today, commoditySymbol, databaseSymbol, monthSymbol, year, currentMonth);
                            parseRawData(dataFile, contract, commoditySymbol, databaseSymbol, monthSymbol, year);
                        } else {
                            System.out.print("-");
                        }
                    }
                    contract = commodity.getContract((year + 1) + monthSymbol);
                    if ((contract == null) || (!today.getTime().equals(contract.getCurrentDate()))) {
                        if (contract == null) {
                            System.out.print("~");
                        } else {
                            System.out.print(".");
                        }
                        dataFile = retrieveFile(today, commoditySymbol, databaseSymbol, monthSymbol, year + 1, false);
                       parseRawData(dataFile, contract, commoditySymbol, databaseSymbol, monthSymbol, year + 1);
                    } else {
                        System.out.print("-");
                    }
                    if (monthNumber <= today.get(Calendar.MONTH)) {
                        contract = commodity.getContract((year + 2) + monthSymbol);
                        if ((contract == null) || (!today.getTime().equals(contract.getCurrentDate()))) {
                            if (contract == null) {
                                System.out.print("~");
                            } else {
                                System.out.print(".");
                            }
//System.out.println("Retrieve " + (year + 2) + monthSymbol);
                            dataFile = retrieveFile(today, commoditySymbol, databaseSymbol, monthSymbol, year + 2, false);
                            parseRawData(dataFile, contract, commoditySymbol, databaseSymbol, monthSymbol, year + 2);
                        } else {
                            System.out.print("-");
                        }
                    }
                }
                monthMask = monthMask >> 1;
                monthNumber++;
            }
            System.out.println("");
        }
        System.out.println("Retrieval of commodities is complete.");

        it = filedData.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            PrintWriter out = null;
            String fileName = "cbot" + key.substring(8,10) + key.substring(0,2) + key.substring(3,5) + ".txt";
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(DailyDataLoaderFactory.dataDir + "\\" + fileName)));
                Iterator it2 = ((TreeSet)filedData.get(key)).iterator();
                while (it2.hasNext()) {
                    out.println((String)it2.next());
                }
            } catch (IOException e) {
                // Dont get any prices
            } finally {
                try { out.close(); } catch (Exception e) {}
            }
        }
    }

    /**
     *  Retrieve the data file from the Internet and save it.
     *
     *  @param  today           Todays date
     *  @param  commoditySymbol The symbol for the commodity to retrieve
     *  @param  databaseSymbol  The database symbol for the commodity to retrieve
     *  @param  monthSymbol     The symbol of the commodity month
     *  @param  year            The year of data to retrieve
     *  @param  currentMonth    Is it the current month
     */
    private static String retrieveFile(Calendar today, String commoditySymbol, String databaseSymbol, String monthSymbol, int year, boolean currentMonth) {
        BufferedReader in = null;
        PrintWriter rawOut = null;
        String page = "";

/*
        String urlName = "http://www.cbot.com/cbot/www/hist_dl/1,2964," + databaseSymbol + "+";
        String y = "" + year;
        String month = monthSymbol + y.substring(2);
        urlName += currentMonth?"1!":month;
        urlName += "+" + month + "+o,00.html";
*/

        String y = "" + year;
        String month = monthSymbol + y.substring(y.length() - 2);
        String urlName = "http://www.cbot.com/cbot/pub/hist_download/1,3333," + databaseSymbol + month + ",00.html";


        String todayDate    = "0" + today.get(Calendar.DATE);
        String todayMonth   = "0" + today.get(Calendar.MONTH);
        String subDirName = "CBOT04-" + (("" + today.get(Calendar.YEAR)).substring(2)) +
                                     (todayMonth.substring(todayMonth.length() - 2)) +
                                     (todayDate.substring(todayDate.length() - 2));
        String fileName = "cbot04-" + (("" + today.get(Calendar.YEAR)).substring(2)) +
                                    (todayMonth.substring(todayMonth.length() - 2)) +
                                    (todayDate.substring(todayDate.length() - 2))
                                    + commoditySymbol + month + ".txt";
        try {
            URL url = new URL(urlName);
            URLConnection conn = url.openConnection();

            StringBuffer priceFile = new StringBuffer();
            String line = null;
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            rawOut = new PrintWriter(new BufferedWriter(new FileWriter(DailyDataLoaderFactory.dataDir + "\\rawdata\\" + subDirName + "\\" + fileName)));

            while ((line = in.readLine()) != (null)) {
                page += line;
                rawOut.println(line);
            }
        } catch (MalformedURLException e) {
            System.out.println("DataLoaderCBOT.MalformedURLException url = " + urlName);
            // Dont get any prices
        } catch (UnknownHostException e) {
            System.out.println("Host unavailable");
            // Host is unavailable.  Dont get any prices.
        } catch (FileNotFoundException e) {
            System.out.println();
            System.out.println("Data file not found at: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Continuing...");
            // Dont get any prices
        } finally {
            try { in.close(); } catch (Exception e) {}
            try { rawOut.close(); } catch (Exception e) {}
        }

        return page;
    }

    /**
     *  Parse page of information and store data lines.
     *
     *  @param  data            the web page to be parsed
     *  @param  contract        the web page to be parsed
     *  @param  commoditySymbol the symbol for the commodity to retrieve
     *  @param  databaseSymbol  the database symbol for the commodity to retrieve
     */
    private static void parseRawData(String data, Contract contract, String commoditySymbol, String databaseSymbol, String monthSymbol, int year) {
        String token = null;
        LinkedList tokenList = new LinkedList();

        CommodityCalendar lastDay = null;
        try {
            if (contract != null) {
                lastDay = new CommodityCalendar();
                lastDay.setTime(dataManager.getLastPriceDateLoaded(contract));
            }
        } catch (Exception e) {
            lastDay = null;
        }

        String y = "" + year;
        String contractMonth = monthSymbol + y.substring(y.length() - 2);
        String d = data.replaceAll("<br>","|");
        d = d.replaceAll("<BR>","|");
        d = d.replaceAll("Date,Open,High,Low,Settle","|");
        StringTokenizer st = new StringTokenizer(d, "|");
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (!token.startsWith("<") &&            // no headers or trailers
                token.indexOf("/") != -1) {
                token = commoditySymbol + "," + contractMonth + "," + token + ",-1,-1";
                token = token.replaceAll(",,",", ,");
                token = token.replaceAll(",,",", ,");   // twice to catch all of the double commas

                tokenList.addFirst(token);
            }
        }

        Iterator it = tokenList.iterator();
        while (it.hasNext()) {
            token = (String)it.next();
            StringTokenizer st2 = new StringTokenizer(token, ",");
            st2.nextToken();    // commodity symbol in file
            st2.nextToken();    // contract
            String priceDate = st2.nextToken();
            st2.nextToken();    // open
            st2.nextToken();    // high
            st2.nextToken();    // low
            st2.nextToken();    // close
            st2.nextToken();    // volume
            st2.nextToken();    // open interst

            if (lastDay != null && !FormatDate.formatDateString(priceDate, "MM/dd/yyyy").after(lastDay.getTime())) {
                continue;
            }

            if (!filedData.containsKey(priceDate)) {
                filedData.put(priceDate, new TreeSet());
            }
            TreeSet mapData = (TreeSet)filedData.get(priceDate);
            mapData.add(token);
        }
    }

    /**
     *  Process the daily CBOT text files by loading them into the database
     *  and backing them up to the directory defined by the folder.commodity_data_backup property
     *  in the dataaccess.properties file.
     *
     *  @param  fileName    the name of the file to be loaded
     */
    protected static void loadPrices(String fileName) throws IOException {
        BufferedReader in = null;
        PrintWriter out = null;
        File        outFile = null;
        String s = null;

        String fileType = null;
        if (fileName.startsWith("bot")) {
            fileType = "bot";
        } else if (fileName.startsWith("cbot")) {
            fileType = "cbot";
        } else {
            return;
        }

        try {
            in = new BufferedReader(new FileReader(DailyDataLoaderFactory.dataDir + "\\" + fileName));

            while ((s = in.readLine()) != null) {
                if ("bot".equals(fileType)) {
//System.out.println("Loading bot");
                    loadFromBOT(s);
                } else if ("cbot".equals(fileType)) {
//System.out.println("Loading cbot");
                    loadFromCBOT(s);
                }
            }
            try { in.close(); } catch (Exception e) {}
            in = new BufferedReader(new FileReader(DailyDataLoaderFactory.dataDir + "\\" + fileName));
            outFile = new File(DailyDataLoaderFactory.backupDir + "\\" + fileName);
            if (outFile.exists()) {
                out = repositionOutFile(outFile);
            } else {
                out = new PrintWriter(new BufferedWriter(new FileWriter(DailyDataLoaderFactory.backupDir + "\\" + fileName)));
            }
            while ((s = in.readLine()) != null) {
                out.println(s);
            }
            try { in.close(); } catch (Exception e) {}
            new File(DailyDataLoaderFactory.dataDir + "\\" + fileName).delete();
        } finally {
            try { in.close(); } catch (Exception e) {}
            try { out.close(); } catch (Exception e) {}
        }
    }

    /**
     *  Reposition the output file to add additional records at the end.
     *
     *  @param  origFile    the output file to reposition to the end
     *  @return         the PrintWriter to continue using
     */
    protected static PrintWriter repositionOutFile(File origFile) throws IOException {
        File renamedFile = null;
        BufferedReader in = null;
        PrintWriter out = null;
        String s = null;

        try {
            String outPath = origFile.getParent();
            String outName = origFile.getName();
            renamedFile = new File(outPath, outName + ".bk");
            origFile.renameTo(renamedFile);

            in = new BufferedReader(new FileReader(renamedFile));
            out = new PrintWriter(new BufferedWriter(new FileWriter(outPath + "\\" + outName)));

            while ((s = in.readLine()) != null) {
                out.println(s);
            }
            return out;

        } finally {
            try { in.close(); } catch (Exception e) {}
            try { renamedFile.delete(); } catch (Exception e) {}
        }
    }

    /**
     *  Load prices from the BOT file format.
     *  This format was valid from 02/01/02 thru 07/03/03.
     *
     *  @param  s       The line from the file to load prices from
     */
    private static void loadFromBOT(String s) {
        StringTokenizer st = null;
        String data = null;

        String symbol = null;
        String contractMonth = null;
        Date date = null;
        double open = 0;
        double high = 0;
        double low = 0;
        double close = 0;
        int    volume = 0;
        int    openint = 0;

        st = new StringTokenizer(s);
        try {
            symbol   = st.nextToken();

            if (Commodity.bySymbol(symbol) == null) {
                return;
            }

            data = st.nextToken();
            if (data.length() == 5) {
                contractMonth = data.substring(1).trim() + data.substring(0,1);
            } else {
                // Contract name is not a standard name to record
                return;
            }
            date     = FormatDate.formatDateString(st.nextToken(), "MM/dd/yyyy");
            open     = PriceFormatter.formatPrice(symbol, Double.parseDouble(st.nextToken()));
            high     = PriceFormatter.formatPrice(symbol, Double.parseDouble(st.nextToken()));
            low      = PriceFormatter.formatPrice(symbol, Double.parseDouble(st.nextToken()));
            close    = PriceFormatter.formatPrice(symbol, Double.parseDouble(st.nextToken()));
            volume = 0;
            openint = 0;
            if (st.hasMoreTokens()) {
                String nextToken = st.nextToken();
                if ("*".equals(nextToken)) {
                    if (st.hasMoreTokens()) {
                        volume   = Integer.parseInt(st.nextToken());
                        openint  = Integer.parseInt(st.nextToken());
                    }
                } else {
                    volume   = Integer.parseInt(nextToken);
                    openint  = Integer.parseInt(st.nextToken());
                }
            }
            if ((high == 0.0) || (low == 0.0)) {
                return;
            }

            Prices prices = new Prices(date, open, high, low, close, volume, openint);
            Contract contract = Commodity.bySymbol(symbol).addContract(contractMonth);
            contract.addPrices(prices);

        } catch (NoSuchElementException e) {
            // Not all of the information was present that is required.
        }
    }

    /**
     *  Load prices from the CBOT file format.
     *  This format was valid from 07/07/03 thru present.
     *
     *  @param  s       The line from the file to load prices from
     */
    private static void loadFromCBOT(String s) {
        StringTokenizer st = null;
        String data = null;

        String symbol = null;
        String contractMonth = null;
        Date date = null;
        double open = 0;
        double high = 0;
        double low = 0;
        double close = 0;
        int    volume = 0;
        int    openint = 0;

        st = new StringTokenizer(s, ",");
        try {
            symbol   = st.nextToken();
            data = st.nextToken();
            contractMonth = "20" + data.substring(1).trim() + data.substring(0,1);
            date     = FormatDate.formatDateString(st.nextToken(), "MM/dd/yyyy");
            data = st.nextToken();
            open     = ("".equals(data.trim()))?0:PriceFormatter.formatPrice(symbol, Double.parseDouble(data));
            data = st.nextToken();
            high     = ("".equals(data.trim()))?0:PriceFormatter.formatPrice(symbol, Double.parseDouble(data));
            data = st.nextToken();
            low      = ("".equals(data.trim()))?0:PriceFormatter.formatPrice(symbol, Double.parseDouble(data));
            data = st.nextToken();
            close    = ("".equals(data.trim()))?0:PriceFormatter.formatPrice(symbol, Double.parseDouble(data));
            data = st.nextToken();
            volume = ("".equals(data.trim()))?0:Integer.parseInt(data);
            if (st.hasMoreTokens()) {
                data = st.nextToken();
                openint = ("".equals(data.trim()))?0:Integer.parseInt(data);
            } else {
                openint = 0;
            }

            if ((high == 0.0) || (low == 0.0)) {
                return;
            }

            Prices prices = new Prices(date, open, high, low, close, volume, openint);
            Contract contract = Commodity.bySymbol(symbol).addContract(contractMonth);
            contract.addPrices(prices);

        } catch (NumberFormatException e) {
            // The values for this line of data are currupt.  Skip the line and continue
        } catch (NoSuchElementException e) {
            // Not all of the information was present that is required.
        }
    }
}

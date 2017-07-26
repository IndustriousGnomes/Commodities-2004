/*  _ Properly Documented
 */
/**
 *  The DataLoaderCBOT processes the data files that originate from the
 *  Chicago Board of Trade (CBOT).
 *
 *  @author J.R. Titko
 */
package prototype.commodities.dataaccess.dataloader;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Properties;

import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.dataaccess.*;

public class DataLoaderCBOT {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The data from all of the pages to reorganize into files by date */
    private static TreeMap filedData = new TreeMap();

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
     *  Retrieve the daily CBOT price files.
     *
     *  @param  today   Today's date
     *  @param  lastDay The last day previously retrieved
     */
    protected static void retrievePrices(Calendar today, Calendar lastDay) {
        String dataFile = null;
        String todayDate    = "0" + today.get(Calendar.DATE);
        String todayMonth   = "0" + today.get(Calendar.MONTH);
        String subDirName = "CBOT" + (("" + today.get(Calendar.YEAR)).substring(2)) +
                                     (todayMonth.substring(todayMonth.length() - 2)) +
                                     (todayDate.substring(todayDate.length() - 2));

        File newDir = new File(DataLoaderFactory.dataDir + "\\rawdata\\" + subDirName);
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
System.out.println("Retrieve " + year + monthSymbol);
                        contract = commodity.getContract(year + monthSymbol);
                        dataFile = retrieveFile(today, commoditySymbol, databaseSymbol, monthSymbol, year, currentMonth);
                        parseRawData(dataFile, contract, commoditySymbol, databaseSymbol);
                    }
System.out.println("Retrieve " + (year + 1) + monthSymbol);
                    contract = commodity.getContract((year + 1) + monthSymbol);
                    dataFile = retrieveFile(today, commoditySymbol, databaseSymbol, monthSymbol, year + 1, false);
                    parseRawData(dataFile, contract, commoditySymbol, databaseSymbol);
                    if (monthNumber <= today.get(Calendar.MONTH)) {
System.out.println("Retrieve " + (year + 2) + monthSymbol);
                        contract = commodity.getContract((year + 2) + monthSymbol);
                        dataFile = retrieveFile(today, commoditySymbol, databaseSymbol, monthSymbol, year + 2, false);
                        parseRawData(dataFile, contract, commoditySymbol, databaseSymbol);
                    }
                }
                monthMask = monthMask >> 1;
                monthNumber++;
            }
        }

        it = filedData.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            PrintWriter out = null;
            String fileName = "cbot" + key.substring(8,10) + key.substring(0,2) + key.substring(3,5) + ".txt";
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(DataLoaderFactory.dataDir + "\\" + fileName)));
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

        String urlName = "http://www.cbot.com/cbot/www/hist_dl/1,2964," + databaseSymbol + "+";
        String y = "" + year;
        String month = monthSymbol + y.substring(2);

        urlName += currentMonth?"1!":month;
        urlName += "+" + month + "+o,00.html";
Debug.println(DEBUG, "retrieveFile urlName = " + urlName);

        String todayDate    = "0" + today.get(Calendar.DATE);
        String todayMonth   = "0" + today.get(Calendar.MONTH);
        String subDirName = "CBOT" + (("" + today.get(Calendar.YEAR)).substring(2)) +
                                     (todayMonth.substring(todayMonth.length() - 2)) +
                                     (todayDate.substring(todayDate.length() - 2));
        String fileName = "cbot" + (("" + today.get(Calendar.YEAR)).substring(2)) +
                                    (todayMonth.substring(todayMonth.length() - 2)) +
                                    (todayDate.substring(todayDate.length() - 2))
                                    + commoditySymbol + month + ".txt";
Debug.println(DEBUG, "retrieveFile fileName = " + fileName);
        try {
            URL url = new URL(urlName);
            URLConnection conn = url.openConnection();

            StringBuffer priceFile = new StringBuffer();
            String line = null;
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            rawOut = new PrintWriter(new BufferedWriter(new FileWriter(DataLoaderFactory.dataDir + "\\rawdata\\" + subDirName + "\\" + fileName)));

            while ((line = in.readLine()) != (null)) {
                page += line;
                rawOut.println(line);
            }
        } catch (MalformedURLException e) {
            System.out.println("DataLoaderCBOT.MalformedURLException url = " + urlName);
            // Dont get any prices
        } catch (UnknownHostException e) {
            // Host is unavailable.  Dont get any prices.
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Continueing...");
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
    private static void parseRawData(String data, Contract contract, String commoditySymbol, String databaseSymbol) {
        String token = null;
        String oldVolume = " ";
        String oldOpenInterest = " ";
        String newVolume = " ";
        String newOpenInterest = " ";
        LinkedList tokenList = new LinkedList();

        CommodityCalendar lastDay = null;
        try {
            if (contract != null) {
                lastDay = new CommodityCalendar();
                lastDay.setTime(DataManagerFactory.getInstance().getLastPriceDateLoaded(contract));
            }
        } catch (Exception e) {
            lastDay = null;
        }

        String d = data.replaceAll("<br>","|");
        d = d.replaceAll("<BR>","|");
        StringTokenizer st = new StringTokenizer(d, "|");
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (!token.startsWith("<")) {               // no headers or trailers
                token = token.replaceAll(",,",", ,");
                token = token.replaceAll(",,",", ,");   // twice to catch all of the double commas
                if (!commoditySymbol.equals(databaseSymbol)) {
                    token = token.replaceFirst(databaseSymbol, commoditySymbol);
                }
                tokenList.addFirst(token);
            }
        }

        Iterator it = tokenList.iterator();
        while (it.hasNext()) {
            oldVolume       = newVolume;
            oldOpenInterest = newOpenInterest;

            token = (String)it.next();
            StringTokenizer st2 = new StringTokenizer(token, ",");
            st2.nextToken();    // commodity symbol in file
            st2.nextToken();    // contract
            String priceDate = st2.nextToken();
            st2.nextToken();    // open
            st2.nextToken();    // high
            st2.nextToken();    // low
            st2.nextToken();    // close
            newVolume       = st2.nextToken();
            newOpenInterest = (st2.hasMoreTokens())?st2.nextToken():" ";

            if (lastDay != null && !FormatDate.formatDateString(priceDate, "MM/dd/yyyy").after(lastDay.getTime())) {
                continue;
            }

            int lastComma       = token.lastIndexOf(",");
            int lastComma2nd    = token.lastIndexOf(",", lastComma - 1);
            String newToken = token.substring(0, lastComma2nd) + "," + oldVolume + "," + oldOpenInterest;

            if (!filedData.containsKey(priceDate)) {
                filedData.put(priceDate, new TreeSet());
            }
            TreeSet mapData = (TreeSet)filedData.get(priceDate);
            mapData.add(newToken);
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
            in = new BufferedReader(new FileReader(DataLoaderFactory.dataDir + "\\" + fileName));

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
            in = new BufferedReader(new FileReader(DataLoaderFactory.dataDir + "\\" + fileName));
            outFile = new File(DataLoaderFactory.backupDir + "\\" + fileName);
            if (outFile.exists()) {
                out = repositionOutFile(outFile);
            } else {
                out = new PrintWriter(new BufferedWriter(new FileWriter(DataLoaderFactory.backupDir + "\\" + fileName)));
            }
            while ((s = in.readLine()) != null) {
                out.println(s);
            }
            try { in.close(); } catch (Exception e) {}
            new File(DataLoaderFactory.dataDir + "\\" + fileName).delete();
        } finally {
            try { in.close(); } catch (Exception e) {}
            try { out.close(); } catch (Exception e) {}
        }
    }

    /**
     *  Reposition the output file to add additional records at the end.
     *
     *  @param  file    the output file to reposition to the end
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
        String contract = null;
        String date = null;
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
                contract = data;
            } else {
                // Contract name is not a standard name to record
                return;
            }
            date     = st.nextToken();
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

            try {
                if (DataLoaderFactory.dataManager.addDailyPrice(symbol, contract, date, open, high, low, close, volume, openint).isAdd()) {
                    Commodity.bySymbol(symbol).clearContracts();
                }
            } catch (IOException e) {
                // Data was already inserted for this day
            }

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
        String contract = null;
        String date = null;
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
            contract = data.substring(0,1) + "20" + data.substring(1);
            date     = st.nextToken();
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

            try {
                if (DataLoaderFactory.dataManager.addDailyPrice(symbol, contract, date, open, high, low, close, volume, openint).isAdd()) {
                    Commodity.bySymbol(symbol).clearContracts();
                }
            } catch (IOException e) {
                // Data was already inserted for this day
            }
        } catch (NumberFormatException e) {
Debug.println(DEBUG, "loadFromCBOT NumberFormatException s = " + s);
            // The values for this line of data are currupt.  Skip the line and continue
        } catch (NoSuchElementException e) {
Debug.println(DEBUG, "loadFromCBOT NoSuchElementException s = " + s);
            // Not all of the information was present that is required.
        }
    }
}
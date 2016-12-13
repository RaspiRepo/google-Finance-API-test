/*----------------------------------------------------------------------------
RaspiRepo
Mounatin View, California, USA.
-----------------------------------------------------------------------------*/
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.sql.Timestamp;

import org.json.*;

class google_finance_api 
/*----------------------------------------------------------------------------
    google_finance_api.java :
                          This main class to check how to download
                          current stock values from google finance API.

    Written By          : RaspiRepo
    Address             : Mountain View, CA 94040

    Date                : September 24, 2014

    Copyright (c) 2014-Present.
    All Rights Reserved.
------------------------------------------------------------------------------*/
{
    public static void main(String[] args) 
    {
        //http://finance.google.com/finance/info?client=ig&q=

         HttpURLConnection   connection    = null;
         OutputStreamWriter  wr            = null;
         BufferedReader      rd            = null;
         URL                 serverAddress = null;

/* Format information 
// [
{
"id": "22144"
,"t" : "AAPL"
,"e" : "NASDAQ"
,"l" : "105.89"
,"l_fix" : "105.89"
,"l_cur" : "105.89"
,"s": "0"
,"ltt":"3:40PM EDT"
,"lt" : "Aug 30, 3:40PM EDT"
,"lt_dts" : "2016-08-30T15:40:25Z"
,"c" : "-0.93"
,"c_fix" : "-0.93"
,"cp" : "-0.87"
,"cp_fix" : "-0.87"
,"ccol" : "chr"
,"pcls_fix" : "106.82"
}

googleFinanceKeyToFullName = {
    u'id'     : u'ID',
    u't'      : u'StockSymbol',
    u'e'      : u'Index',
    u'l'      : u'LastTradePrice',
    u'l_cur'  : u'LastTradeWithCurrency',
    u'ltt'    : u'LastTradeTime',
    u'lt_dts' : u'LastTradeDateTime',
    u'lt'     : u'LastTradeDateTimeLong',
    u'div'    : u'Dividend',
    u'yld'    : u'Yield',
    u's'      : u'LastTradeSize',
    u'c'      : u'Change',
    u'c'      : u'ChangePercent',
    u'el'     : u'ExtHrsLastTradePrice',
    u'el_cur' : u'ExtHrsLastTradeWithCurrency',
    u'elt'    : u'ExtHrsLastTradeDateTimeLong',
    u'ec'     : u'ExtHrsChange',
    u'ecp'    : u'ExtHrsChangePercent',
    u'pcls_fix': u'PreviousClosePrice'
}

]*/

        try {
            String symbol_list = "aapl,goog,msft,fb";
            serverAddress = new URL("https://finance.google.com/finance/info?client=ig&q=" + symbol_list);
            connection = (HttpURLConnection)serverAddress.openConnection();

            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(1000);
            connection.connect();

            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            int data = inputStreamReader.read();
            String sym_realtime_info = "";
            while(data != -1){
                sym_realtime_info += (char) data;
                data = inputStreamReader.read();
            }

            String str_j = sym_realtime_info.replace("//", "");

            //loop thru all json object list for each symbol 
            JSONArray stock_sym_list = new JSONArray(str_j);

            int index = 0;
            JSONObject stcok_obj = null;
            String div_yld = "";

            while (index < stock_sym_list.length()) {
                stcok_obj = new JSONObject(stock_sym_list.get(index).toString());
                String symbol = stcok_obj.getString("t");
                div_yld = "";
                if (stcok_obj.has("div") && !stcok_obj.getString("div").isEmpty()) {
                    div_yld = Double.parseDouble(stcok_obj.getString("div")) + "";
                }

                if (stcok_obj.has("yld") && !stcok_obj.getString("yld").isEmpty()) {
                    div_yld += "/" + Double.parseDouble(stcok_obj.getString("yld"));
                }

                Double curr_price = stcok_obj.getDouble("l_cur");
                Double prev_close_price = stcok_obj.getDouble("pcls_fix");
                Double gain_loss = curr_price - prev_close_price;

                System.out.println(symbol + " " + curr_price + " " + prev_close_price + " " + String.format( "%.2f", gain_loss) + " " + div_yld);
                ++index;
            }

            Thread.sleep(100);

            connection.disconnect();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
          } catch (MalformedURLException e) {
              System.out.println(e.getMessage());
          } catch (ProtocolException e) {
              System.out.println(e.getMessage());
          } catch (IOException e) {
              System.out.println(e.getMessage());
          } catch (InterruptedException e) {
              System.out.println(e.getMessage());
          }
    }
}

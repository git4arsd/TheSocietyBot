package nationwide.co.uk.thesocietybot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ApiService {

    URL url;
    HttpURLConnection urlConnection = null;
    MessageContent msgcontent;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH.mm");
    private static final String[] Cust_Accounts = new String[]{
            "444123:12447675,94773361", "444124:20013726,85123885"
    };

    public String getAccountBalance(String id){

        String oburl = "https://xyz.com/api/accounts/"+id+"/balances";

        String resp = getHTTPResponse(oburl);
        String output="";

        try {
            JSONObject jobj = new JSONObject(resp);
            jobj = jobj.getJSONArray("Data").getJSONObject(0);
            jobj = jobj.getJSONObject("Balance").getJSONObject("Amount");
            String balance = jobj.getString("Amount") +" "+ jobj.getString("Currency");
//            msgcontent.setUserId("received");
//            msgcontent.setCreatedAt(sdf.format(Calendar.getInstance().getTime()));
            output = "Your Account Balance for "+id+" is "+balance;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

    public String getCustomerAccountBalances(String id){

        String custData = "",text="";

        try {
            for (String custId : Cust_Accounts){
                if (custId.contains(id)) {
                    custData = custId;
                }
            }
            String custAccounts[] = custData.split(":");
            custAccounts = custAccounts[1].split(",");

            for (String account:custAccounts){
               text= text + getAccountBalance(account) + "\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return text;
    }

    public String getAccountProduct(String id) {

        String oburl = "https://xyz.com/api/accounts/"+id+"/product";

        String resp = getHTTPResponse(oburl);
        String output="";

        try {
            JSONObject jobj = new JSONObject(resp);
            jobj = jobj.getJSONArray("Data").getJSONObject(0);
            jobj = jobj.getJSONObject("Product");

            String product = "\nProduct name : "+ jobj.getString("ProductName") +"\nProduct type : "+ jobj.getString("ProductType");
//            msgcontent.setUserId("received");
//            msgcontent.setCreatedAt(sdf.format(Calendar.getInstance().getTime()));
            output = "Your Prodcut Details are as follows\n"+"AccountID : "+id+product;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;

    }

    public String getCustomerProducts(String id) {

        String custData = "",text="";

        try {
            for (String custId : Cust_Accounts){
                if (custId.contains(id)) {
                    custData = custId;
                }
            }
            String custAccounts[] = custData.split(":");
            custAccounts = custAccounts[1].split(",");

            for (String account:custAccounts){
                text= text + getAccountProduct(account) + "\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public String getCustomerLasttransaction(String id){

        String custData = "",text="";

        try {
            for (String custId : Cust_Accounts){
                if (custId.contains(id)) {
                    custData = custId;
                }
            }
            String custAccounts[] = custData.split(":");
            custAccounts = custAccounts[1].split(",");

            text= text + getAccountTransaction(custAccounts[0]) + "\n";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;

    }

    public String getAccountTransaction(String id){

        String oburl = "https://xyz.com/api/accounts/"+id+"/transactions";

        String resp = getHTTPResponse(oburl);
        String output="";

        try {
            JSONObject jobj = new JSONObject(resp);
            jobj = jobj.getJSONArray("Data").getJSONObject(0);
            jobj = jobj.getJSONObject("Transaction");
            String trans_ref = "\nTransaction Reference : "+jobj.getString("TransactionReference");
            String amount = "\nAmount : " + jobj.getJSONObject("Amount").getString("Amount");
            String balance = "\nBalance : "+jobj.getJSONObject("Balance").getString("Amount");
            String bookDate = "\nBookingDateTime : "+jobj.getString("BookingDateTime");
            String status = "\nStatus : " + jobj.getString("Status");
            String merchant = "\nMerchantName : " + jobj.getString("MerchantName");
//            msgcontent.setUserId("received");
//            msgcontent.setCreatedAt(sdf.format(Calendar.getInstance().getTime()));
            output = "Your Last transaction Details are as follows\n"+"AccountID : "+id+trans_ref+amount+balance+bookDate+status+merchant;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

    public String getHTTPResponse(String uri){

        StringBuffer response = new StringBuffer();

        try {
            url = new URL(uri);
            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();

            BufferedReader bf = new BufferedReader(new InputStreamReader(in));

            bf = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = bf.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response.toString();
    }
}

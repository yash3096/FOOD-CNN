package food.com.food;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FinalRecipiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_recipi);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String value = extras.getString("id_to_get_url");
            Toast.makeText(this, value, Toast.LENGTH_LONG).show();
            //The key argument here must match that used in the other activity
            new PostAsynTask().execute(value);
        }
    }
    public class PostAsynTask extends AsyncTask<String,Void,String>
    {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("Final recipe available at: "+s);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
            startActivity(browserIntent);
        }

        @Override
        protected String doInBackground(String... bis) {
            JSONObject jsonObject= new JSONObject();
            String srcurl="";
            try {
                //  Thread.sleep(10000);
                System.out.println("entered background");
                String path=bis[0];
                System.out.println(path);
                String urlstring1="http://api.yummly.com/v1/api/recipe/"+path+"?_app_id=ca9b04a1&_app_key=aa47d213817c482b33c44295f269d3f2";
                URL url = new URL(urlstring1);
                System.out.println(urlstring1);
                //super class is URLConnection open connection returns object of superClass
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //post method told by server docs to be used to add new finding to server
                System.out.println("HI1");
                connection.setRequestMethod("GET");
                //telling URLConnection class that we are sending some output as data and also recieving some input as data
                connection.setDoOutput(false);
                connection.setDoInput(true);
                //making JSON data type to be sent to server side
                    /*String data=String.format("{\"age\":%d,\"height\":%.1f,\"address\":\"%s\"}",
                        findings.getAge(),findings.getHeight(),findings.getAddress());*/
                //proxy of above one
                //path="tV5L8qwIYwQJgQjlYnxxZVacPMkLCXR21lezeVGW63f3rk7OiqKorzV/5Vf/6/7lbDQYp2lWMZz/"
                //its a header that  we are sending the info to the server that we will send data which would be in json format
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                //for sending json data to server use printwriter object print data and flush it
                //response code is sent by server side to client which should be
                // equal to predefined constant in HttpUrlconnection class
                connection.connect();
                int statuscode = connection.getResponseCode();
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                String jsonString = sb.toString();
                System.out.println(jsonString);
                try {
                    jsonObject=new JSONObject(jsonString);
                    JSONArray nutrients=jsonObject.getJSONArray("nutritionEstimates");
                    jsonObject=jsonObject.getJSONObject("source");
                    System.out.println("Final source: "+jsonObject);
                    srcurl=jsonObject.getString("sourceRecipeUrl");
                    System.out.println("Srcurl:"+ srcurl);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //do not forget to disconnect your http connection with server
                System.out.println("HI5");


                System.out.println("Status code:" + statuscode);
                connection.disconnect();


            } catch (IOException e) {
                System.out.println(e);


                e.printStackTrace();
            }
            return srcurl;
        }
    }
}

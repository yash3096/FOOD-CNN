package food.com.food;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CalorificValue extends AppCompatActivity {

    private ProgressBar progressBar;
    private JSONObject jsonObject;
    TableLayout tableLayout;
    JSONArray nutrients;
    private TextView calories,carbs,sugar,fats;
    private TextView protien;
    String ndbno,food;
    //TableRow tableRow,getTableRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorific_value);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ndbno = extras.getString("NDB_NO");
            food = extras.getString("food_classified");
            Toast.makeText(this,"Food found: "+food,Toast.LENGTH_LONG).show();
            //The key argument here must match that used in the other activity
        }

        calories= (TextView) findViewById(R.id.tv_calories);
        carbs= (TextView) findViewById(R.id.tv_carbs);
        fats= (TextView) findViewById(R.id.tv_fats);
        sugar= (TextView) findViewById(R.id.tv_sugar);
        protien=(TextView) findViewById(R.id.tv_protien);
        progressBar= (ProgressBar) findViewById(R.id.progressbar);
        new PostSendingAsynTask().execute(ndbno);
    }
    public void onRecipe(View view)
    {
        if(food!=null)
        {Intent intent = new Intent(getBaseContext(), RecipeActivity.class);
        intent.putExtra("food_classified", food);
        startActivity(intent);}
        else
            Toast.makeText(this,"Bro food ka category nahi mila",Toast.LENGTH_LONG).show();

    }
    class PostSendingAsynTask extends AsyncTask<String,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            progressBar.setVisibility(View.INVISIBLE);

            for(int i=0;i<nutrients.length();i++)
            {
                JSONObject object= null;
                try {
                    object = nutrients.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    String id=object.getString("nutrient_id");
                    switch (id) {
                        case "208":
                            calories.setText(object.getString("value")+" "+object.getString("unit"));
                            System.out.println("calories found");
                            break;
                        case "203":
                            protien.setText(object.getString("value") + " " + object.getString("unit"));
                            System.out.println("protien found");
                            break;
                        case "204":
                            fats.setText(object.getString("value") + " " + object.getString("unit"));
                            System.out.println("fats found");
                            break;
                        case "205":
                            carbs.setText(object.getString("value")+" "+object.getString("unit"));
                            System.out.println("Carbs found");
                            break;
                        case "269":
                            sugar.setText(object.getString("value")+" "+object.getString("unit"));
                            System.out.println("sugars found");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Void doInBackground(String... bis) {
            try {
              //  Thread.sleep(10000);
                System.out.println("entered background");
                String path=bis[0];
                //  urlstring="https://api.nal.usda.gov/ndb/search/?format=json&q="+path+"&sort=n&max=25&offset=0&api_key=9iytT5nrdPD3gTRLZRUIyRlYonP7h7UC4s6ci1rt";
                String urlstring1="https://api.nal.usda.gov/ndb/V2/reports?ndbno="+path+"&type=b&format=json&api_key=9iytT5nrdPD3gTRLZRUIyRlYonP7h7UC4s6ci1rt";
                URL url = new URL(urlstring1);
                System.out.println("calorieurl: "+urlstring1);
                //super class is URLConnection open connection returns object of superClass
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //post method told by server docs to be used to add new finding to server
                System.out.println("HI1");
                connection.setRequestMethod("GET");
                //telling URLConnection class that we are sending some output as data and also recieving some input as data
                connection.setDoOutput(true);
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

                        //System.out.println(jsonObject);
                        JSONArray foods=jsonObject.getJSONArray("foods");
                        jsonObject=(new JSONObject(foods.getString(0)));
                        jsonObject=jsonObject.getJSONObject("food");
                        nutrients=jsonObject.getJSONArray("nutrients");
                        System.out.println("foods:\t");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                //do not forget to disconnect your http connection with server
                System.out.println("HI5");

                connection.disconnect();

                System.out.println("Status code:" + statuscode);

            } catch (IOException e) {
                System.out.println(e);


                e.printStackTrace();
            }
            return null;
        }
    }
}

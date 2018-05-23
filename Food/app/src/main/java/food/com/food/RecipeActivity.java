package food.com.food;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String food;
    CustomAdapter1 adapter;
    JSONObject jsonObject;
    ListView listView;
    ArrayList<RecipeOption> list;
    String urlcall;
    Context context;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        Bundle extras = getIntent().getExtras();
        context=this;
        if (extras != null) {
            food = extras.getString("food_classified");
            Toast.makeText(this,"Food found: "+food,Toast.LENGTH_LONG).show();
            //The key argument here must match that used in the other activity
        }

        progressBar= (ProgressBar) findViewById(R.id.progressbar);
        list=new ArrayList<RecipeOption>();
        listView = (ListView) findViewById(R.id.recipe_list);
        adapter = new CustomAdapter1();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
        new PostSendingAsynTask().execute(food);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this,list.get(position).getId(),Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), FinalRecipiActivity.class);
        intent.putExtra("id_to_get_url", list.get(position).getId());
        startActivity(intent);
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
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            Toast.makeText(context, "Low Carb " + food + " Recipe for diabetics", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... bis) {
            try {
                //  Thread.sleep(10000);
                System.out.println("entered background");
                String path=bis[0];
                String urlstring1="http://api.yummly.com/v1/api/recipes?_app_id=ca9b04a1&_app_key=aa47d213817c482b33c44295f269d3f2&q=low+carb+"+path;
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
                    JSONArray foods=jsonObject.getJSONArray("matches");
                    System.out.println("recipe: "+foods);
                    for(int i=0;i<foods.length();i++)
                    {
                        JSONObject object=foods.getJSONObject(i);
                        JSONArray ingredients=object.getJSONArray("ingredients");
                        String finalingredients="";
                        for(int j=0;j<ingredients.length();j++)
                            finalingredients+=ingredients.getString(j)+",";
                        System.out.println("final ingredients "+finalingredients);
                        list.add(new RecipeOption(object.getString("recipeName"),finalingredients,object.getString("sourceDisplayName"),object.getString("id")));
                    }

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
            return null;
        }
    }

    class  CustomAdapter1 extends BaseAdapter
    {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            RecipeOptionsHolder holder=null;
            if(view==null)
            {
                LayoutInflater layoutInflater=getLayoutInflater();
                view=layoutInflater.inflate(R.layout.recipelistelement,null);
                holder=new RecipeOptionsHolder();
                holder.name=(TextView)view.findViewById(R.id.tv_recipe_name);
                holder.ingredients=(TextView)view.findViewById(R.id.tv_ingredients);
                holder.source=(TextView)view.findViewById(R.id.tv_source);
                view.setTag(holder);
            }
            else
            {
                holder=(RecipeOptionsHolder)view.getTag();
            }
            RecipeOption recipeOption= list.get(i);
            holder.name.setText(recipeOption.getName()+"");
            holder.ingredients.setText(recipeOption.getIngredients() + "");
            holder.source.setText(recipeOption.getSource() + "");
            return view;
        }

        @Override
        public CharSequence[] getAutofillOptions() {
            return new CharSequence[0];
        }
    }
    class RecipeOptionsHolder
    {
        TextView name,ingredients,source;
    }
}

package food.com.food;

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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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

public class FoodList extends AppCompatActivity implements AdapterView.OnItemClickListener{

    CustomAdapter adapter;
    JSONObject jsonObject;
    ListView listView;
    ArrayList<FoodOptions> list;
    String urlcall;
    String value[];
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            value = extras.getStringArray("food_classified");
//            Toast.makeText(this,value[0],Toast.LENGTH_LONG).show();
            //The key argument here must match that used in the other activity
        }

        list=new ArrayList<FoodOptions>();
        listView = (ListView) findViewById(R.id.food_list);
        adapter = new CustomAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
        progressBar= (ProgressBar) findViewById(R.id.progressbar);

        /*value=new String[2];
        value[0]="dominos%20pizza";
        value[1]="vegetable%20samosa";*/
        for(int  i=0;i<value.length;i++)
        new PostSendingAsynTask().execute(value[i]);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this,list.get(position).getType(),Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), CalorificValue.class);
        intent.putExtra("NDB_NO", list.get(position).getNdbno());
        intent.putExtra("food_classified",list.get(position).getType());
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
        }

        @Override
        protected Void doInBackground(String... bis) {
            try {
                //  Thread.sleep(10000);
                System.out.println("entered background");
                String path=bis[0];
                String urlstring1="https://api.nal.usda.gov/ndb/search/?format=json&q="+path+"&sort=n&max=25&offset=0&api_key=9iytT5nrdPD3gTRLZRUIyRlYonP7h7UC4s6ci1rt";
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
                    //System.out.println(jsonObject);
                    jsonObject=jsonObject.getJSONObject("list");
                    JSONArray foods=jsonObject.getJSONArray("item");
                    for(int i=0;i<foods.length();i++)
                    {
                        JSONObject object=foods.getJSONObject(i);
                        list.add(new FoodOptions(object.getString("group"),object.getString("name"),object.getString("ndbno"),path));
                    }


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

    class  CustomAdapter extends BaseAdapter
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
            FoodOptionsHolder holder=null;
            if(view==null)
            {
                LayoutInflater layoutInflater=getLayoutInflater();
                view=layoutInflater.inflate(R.layout.foodlistelement,null);
                holder=new FoodOptionsHolder();
                holder.group=(TextView)view.findViewById(R.id.tv_group);
                holder.name=(TextView)view.findViewById(R.id.tv_food_name);
                holder.ndbno=(TextView)view.findViewById(R.id.tv_ndbno);
                view.setTag(holder);
            }
            else
            {
                holder=(FoodOptionsHolder)view.getTag();
            }
            FoodOptions foodOption= list.get(i);
            holder.name.setText(foodOption.getName()+"");
            holder.group.setText(foodOption.getGroup()+"");
            holder.ndbno.setText(foodOption.getNdbno()+"");
            return view;
        }

        @Override
        public CharSequence[] getAutofillOptions() {
            return new CharSequence[0];
        }
    }
    class FoodOptionsHolder
    {
        TextView name,group,ndbno;
    }
    /*class CustomAdpater extends BaseAdapter
    {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public FoodOptions getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FindingsHolder holder=null;
            if(convertView== null)
            {
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.foodlistelement.xml, null);
                holder=new FindingsHolder();
                holder.group= (TextView) convertView.findViewById(R.id.tv_group);
                holder.name= (TextView) convertView.findViewById(R.id.tv_name);
                holder.ndbno= (TextView) convertView.findViewById(R.id.tv_ndbno);
                convertView.setTag(holder);
            }
            else
            {
                holder= (FindingsHolder) convertView.getTag();
            }
            Findings findings=list.get(position);
            holder.age.setText(findings.getAge()+"");
            holder.height.setText(findings.getHeight() + "");
            holder.address.setText(findings.getAddress()+"");
            return convertView;
        }
    }*/
}

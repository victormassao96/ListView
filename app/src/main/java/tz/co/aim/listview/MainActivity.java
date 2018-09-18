package tz.co.aim.listview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listView;


//    URL for getting contacts
    private String url = "https://api.androidhive.info/contacts/";
    ArrayList<HashMap<String,String>>contactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        listView = (ListView)findViewById(R.id.list_view);

        new GetContacts().execute();

    }
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler handler = new HttpHandler();


            String jsonString = handler.makeServiceCall(url);

            Log.e(TAG, "Response from url:" +jsonString);
            if (jsonString !=null){
                try {

                    JSONObject jsonObject1 = new JSONObject(jsonString);
                    JSONArray contacts = jsonObject1.getJSONArray("contacts");




                    for (int i = 0; i < contacts.length();i++){

                        JSONObject jsonObject = contacts.getJSONObject(i);



                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String email = jsonObject.getString("email");
                        String address = jsonObject.getString("address");
                        String gender = jsonObject.getString("gender");


                        JSONObject phone = jsonObject.getJSONObject("phone");

                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");


                        HashMap<String, String>contact = new HashMap<>();


                        contact.put("id",id);
                        contact.put("name",name);
                        contact.put("email", email);
                        contact.put("address", address);
                        contact.put("gender", gender);
                        contact.put("mobile", mobile);


                        contactList.add(contact);
                    }
                }catch (final JSONException e){
                    Log.e(TAG, "Json parsing error: "+e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing  error: "+e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });
                }


            }else {
                Log.e(TAG, "Couldn't get Json from Server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't get Json from Server.",Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;


        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if (pDialog.isShowing());
                pDialog.dismiss();

            ListAdapter listAdapter = new SimpleAdapter(
                    MainActivity.this, contactList, R.layout.list_item,new String[]{"name","email","mobile","address","gender"},
                    new int[]{R.id.name,R.id.email,R.id.mobile,R.id.address,R.id.gender}
            );

            listView.setAdapter(listAdapter);
        }
    }
}

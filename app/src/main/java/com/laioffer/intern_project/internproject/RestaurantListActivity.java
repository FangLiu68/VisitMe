package com.laioffer.intern_project.internproject;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RestaurantListActivity extends Activity {

    public final static String RESTAURANT_LOC = "com.laioffer.internproject.RESTAURANT_LOC";
    private static final String BASE_URL = "http://172.17.193.69:8080/Rest/";

    public ProgressDialog progressDialog;
    RestaurantListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_restaurant_list);
        fragment = (RestaurantListFragment) getFragmentManager().findFragmentByTag("fragtag");
    }

    /**
     * Use this method to instantiate the action bar, and add your items to it.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant_list, menu);

        // It should return true if you have added items to it and want the menu
        // to be displayed.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            // Implement the action here!
            return true;
        } else if (id == R.id.action_location) {
            switchToMapView(restaurant_loc);
        } else if (id == R.id.action_recommend) {
            recommendRestaurants();
        }

        return super.onOptionsItemSelected(item);
    }

    // This is main function to call backend service.
    // We'd better create a new class Restaurant to represent and contain all
    // the data of one restaurant.
    private void getNearbyRestaurants() {

    }

    private void recommendRestaurants() {
        RecommendRestaurants t=new RecommendRestaurants();
        t.execute("RecommendRestaurants");
    }

    // Create the intent and switch to map view activity.
    private void switchToMapView(ArrayList<LatLng> restaurant_loc) {
        Intent intent = new Intent(this, RestaurantMapActivity.class);

        // Fake some loc data here.
        for (int i = 0; i < 5; ++i) {
            restaurant_loc.add(i, new LatLng(i, i));
        }

        intent.putParcelableArrayListExtra(RESTAURANT_LOC, restaurant_loc);
        startActivity(intent);
    }

    // Store the locations of all restaurants.
    private ArrayList<LatLng> restaurant_loc = new ArrayList<>();

    private static List<Restaurant> POST(String[] url){
        InputStream inputStream = null;
        List<Restaurant> result = new ArrayList<>();
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(BASE_URL + url[0]);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("user_id", "1111");

            switch (url[0]) {
                case "GetRestaurantsNearby":
                    jsonObject.accumulate("lat", 30.1);
                    jsonObject.accumulate("lon", 20.2);
                    break;
                case "SetVisitedRestaurants":
                    JSONArray array = new JSONArray();
                    array.put(url[1]);
                    jsonObject.accumulate("visited", array);
                    break;
                case "RecommendRestaurants":
                    break;
            }

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                String str = convertInputStreamToString(inputStream);
                JSONArray array = new JSONArray(str);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    result.add(new Restaurant(object));
                }

            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

        private List<Restaurant> itemList;
        private Activity activity;
        RestaurantListAdapter(Activity activity,List<Restaurant> itemList) {
            super(activity, R.layout.fragment_restaurant_list_item, R.id.restaurant_name,
                    itemList);
            this.itemList = itemList;
            this.activity = activity;
        }
        private class ViewHolder {
            TextView titleText;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Restaurant item = getItem(position);
            View viewToUse = null;

            // This block exists to inflate the settings list item conditionally based on whether
            // we want to support a grid or list view.
            LayoutInflater mInflater = (LayoutInflater) activity
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                viewToUse = mInflater.inflate(R.layout.fragment_restaurant_list_item,  null);
                holder = new ViewHolder();
                holder.titleText = (TextView)viewToUse.findViewById(R.id.restaurant_name);
                viewToUse.setTag(holder);
            } else {
                viewToUse = convertView;
                holder = (ViewHolder) viewToUse.getTag();
            }

            holder.titleText.setText(item.getName());
            return viewToUse;
        }
    }

    private class RecommendRestaurants extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return POST(params);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Restaurant> result) {
            super.onPostExecute(result);
            fragment.adapter.itemList.clear();
            fragment.adapter.itemList.addAll(result);
            if (fragment.getListAdapter() == fragment.adapter) {
                Log.i("Fragment list Adapter", "equals to ");
            }
            //fragment.setListAdapter(adapter);
            fragment.adapter.notifyDataSetChanged();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }
}

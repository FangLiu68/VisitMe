package com.laioffer.intern_project.internproject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RestaurantListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static RestaurantListFragment newInstance(String param1, String param2) {
        RestaurantListFragment fragment = new RestaurantListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantListFragment() {
    }

    private RestaurantListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the ListAdapter.
        adapter = new RestaurantListAdapter(getActivity(), new ArrayList<Restaurant>());
        GetRestaurantsNearbyAsyncTask task = new GetRestaurantsNearbyAsyncTask();
        task.execute("GetRestaurantsNearby");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Restaurant item = adapter.itemList.get(position);

        Log.i("Position is ", item.getBusinessId());
        SetVisitedRestaurantsAsyncTask task = new SetVisitedRestaurantsAsyncTask();
        task.execute("SetVisitedRestaurants", item.getBusinessId());
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            // mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

        private List<Restaurant> itemList;
        private Context context;
        RestaurantListAdapter(Context context, List<Restaurant> itemList) {
            super(getActivity(), R.layout.fragment_restaurant_list_item, R.id.restaurant_name,
                    itemList);
            this.itemList = itemList;
            this.context = context;
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
            LayoutInflater mInflater = (LayoutInflater) context
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

    private static final String BASE_URL = "http://192.168.1.3:8080/Rest/";

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

    private class GetRestaurantsNearbyAsyncTask extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return POST(params);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Restaurant> result) {
            super.onPostExecute(result);
            adapter.itemList.clear();
            adapter.itemList.addAll(result);
            setListAdapter(adapter);
        }
    }

    private class SetVisitedRestaurantsAsyncTask extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return POST(params);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Restaurant> result) {
            Log.i("SetVisited", "Done");
            super.onPostExecute(result);
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

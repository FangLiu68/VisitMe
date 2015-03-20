package com.laioffer.intern_project.internproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class RestaurantListActivity extends Activity {

    public final static String RESTAURANT_LOC = "com.laioffer.internproject.RESTAURANT_LOC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_restaurant_list);
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
        }

        return super.onOptionsItemSelected(item);
    }

    // This is main function to call backend service.
    // We'd better create a new class Restaurant to represent and contain all
    // the data of one restaurant.
    private void getNearbyRestaurants() {

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
}

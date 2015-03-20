package com.laioffer.intern_project.internproject;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the restaurant list here.
        // Let's make up some fake restaurant name here!
        ArrayList<String> restaurants = new ArrayList<String>();
        restaurants.add("Shanghai Restaurant");
        restaurants.add("Yunnan Garden");
        restaurants.add("Chengdu Tasty");
        restaurants.add("Boiling Crab");
        restaurants.add("Malan Noodle");
        restaurants.add("Little Sheep Hotpot");

        // Set the ListAdapter.
        setListAdapter(new RestaurantListAdapter(restaurants));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // try {
        //     mListener = (OnFragmentInteractionListener) activity;
        // } catch (ClassCastException e) {
        //     throw new ClassCastException(activity.toString()
        //             + " must implement OnFragmentInteractionListener");
        // }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

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

    class RestaurantListAdapter extends ArrayAdapter<String> {

        RestaurantListAdapter(ArrayList<String> restaurants) {
            super(getActivity(), R.layout.fragment_restaurant_list_item, R.id.restaurant_name,
                  restaurants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // Let ArrayAdapter inflate the layout and set the text
            View view = super.getView(position, convertView, container);

            // Finally return the view to be displayed
            return view;
        }
    }

}

package com.nattysoft.navigationdrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nattysoft.navigationdrawer.util.Preferences;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentTwo extends Fragment {

	public static final String IMAGE_RESOURCE_ID = "iconResourceID";
	public static final String ITEM_NAME = "itemName";

	ListView listView;
	View view;
	private int pos = 0;
	private JSONObject jsonObject;
	private JSONArray housesArray;
	private String[] values;

	public FragmentTwo() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_layout_two, container, false);

		Bundle args = getArguments();
		if (args != null) {
			pos = args.getInt("position");
		}

		try {
			jsonObject = new JSONObject(Preferences.getPreference(FragmentTwo.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON + ":" + pos));
			housesArray = jsonObject.getJSONArray("houses");
			values = new String[housesArray.length()];// { "900 Salinga Crescent", "793 Missori Street" };
			for (int i = 0; i < housesArray.length(); i++) {
				values[i] = housesArray.getJSONObject(i).getString("address");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Get ListView object from xml
		listView = (ListView) view.findViewById(R.id.addresses_list);

		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		listView.setAdapter(adapter);

		// ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// ListView Clicked item index
				int itemPosition = position;

				// ListView Clicked item value
				String itemValue = (String) listView.getItemAtPosition(position);

				// Show Alert
				// Toast.makeText(getApplicationContext(), "Position :" +
				// itemPosition + "  ListItem : " + itemValue,
				// Toast.LENGTH_LONG).show();

				Intent startMeterNumberCapture = new Intent(view.getContext(), MeterNumberCapture.class);
				startMeterNumberCapture.putExtra("title", itemValue);
				startMeterNumberCapture.putExtra("position", pos);
				//startMeterNumberCapture.putExtra("housesIndex", housesIndex);
				Preferences.savePreference(FragmentTwo.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + pos, "" + position);
				startActivity(startMeterNumberCapture);

			}

		});

		return view;
	}

}
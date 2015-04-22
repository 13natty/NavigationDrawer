package com.nattysoft.navigationdrawer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.nattysoft.navigationdrawer.listener.RequestResponseListener;
import com.nattysoft.navigationdrawer.util.Preferences;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MeterNumberCapture extends Activity implements RequestResponseListener {

	private int TAKE_PHOTO_CODE = 0;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	CustomDrawerAdapter adapter;

	List<DrawerItem> dataList;
	private String value;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_capture);

		value = "";

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			value = intent.getStringExtra("title");
			position = intent.getIntExtra("position", 0);
		}

		// Initializing
		dataList = new ArrayList<DrawerItem>();
		mTitle = mDrawerTitle = value;
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			SelectItem(0);
		}

		dataList.clear();
		dataList.add(new DrawerItem("Zones", R.drawable.ic_action_email));
		dataList.add(new DrawerItem("Capture", R.drawable.ic_action_good));
		dataList.add(new DrawerItem("View List", R.drawable.ic_action_gamepad));
		dataList.add(new DrawerItem("Navigate", R.drawable.ic_action_labels));

		adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);

		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void SelectItem(int possition) {

		Fragment fragment = null;
		Bundle args = new Bundle();
		switch (possition) {
		case 0:
			// Add Drawer Item to dataList
			dataList.clear();
			dataList.add(new DrawerItem("Zones", R.drawable.ic_action_email));
			dataList.add(new DrawerItem("Capture", R.drawable.ic_action_good));
			dataList.add(new DrawerItem("View List", R.drawable.ic_action_gamepad));
			dataList.add(new DrawerItem("Navigate", R.drawable.ic_action_labels));

			adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);

			mDrawerList.setAdapter(adapter);

			mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
			fragment = new FragmentOne();
			args.putInt("position", position);
			args.putString(FragmentOne.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentOne.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 1:
			// Add Drawer Item to dataList
			dataList.clear();
			dataList.add(new DrawerItem("Zones", R.drawable.ic_action_email));
			dataList.add(new DrawerItem("Capture", R.drawable.ic_action_good));

			adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);

			mDrawerList.setAdapter(adapter);

			mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
			fragment = new FragmentTwo();
			args.putInt("position", position);
			args.putString(FragmentTwo.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentTwo.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 2:
			fragment = new FragmentThree();
			args.putString(FragmentThree.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentThree.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 3:
			fragment = new FragmentOne();
			args.putString(FragmentOne.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentOne.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 4:
			fragment = new FragmentTwo();
			args.putString(FragmentTwo.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentTwo.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 5:
			fragment = new FragmentThree();
			args.putString(FragmentThree.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentThree.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 6:
			fragment = new FragmentOne();
			args.putString(FragmentOne.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentOne.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 7:
			fragment = new FragmentTwo();
			args.putString(FragmentTwo.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentTwo.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 8:
			fragment = new FragmentThree();
			args.putString(FragmentThree.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentThree.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 9:
			fragment = new FragmentOne();
			args.putString(FragmentOne.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentOne.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 10:
			fragment = new FragmentTwo();
			args.putString(FragmentTwo.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentTwo.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 11:
			fragment = new FragmentThree();
			args.putString(FragmentThree.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentThree.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		case 12:
			fragment = new FragmentOne();
			args.putString(FragmentOne.ITEM_NAME, dataList.get(possition).getItemName());
			args.putInt(FragmentOne.IMAGE_RESOURCE_ID, dataList.get(possition).getImgResID());
			break;
		default:
			break;
		}

		fragment.setArguments(args);
		FragmentManager frgManager = getFragmentManager();
		frgManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		mDrawerList.setItemChecked(possition, true);
		setTitle(dataList.get(possition).getItemName());
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return false;
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position == 0) {
				Intent startMeterNumberCapture = new Intent(MeterNumberCapture.this, ZonesActivity.class);
				startActivity(startMeterNumberCapture);
			} else if (position == 3) {

				if (Preferences.getPreference(MeterNumberCapture.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + MeterNumberCapture.this.position) != null) {
					int index = Integer.parseInt(Preferences.getPreference(MeterNumberCapture.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + MeterNumberCapture.this.position));

					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(Preferences.getPreference(MeterNumberCapture.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON + ":" + MeterNumberCapture.this.position));

						JSONArray housesArray = jsonObject.getJSONArray("houses");
						double latitude = housesArray.getJSONObject(index).getDouble("latitude");
						double longitude = housesArray.getJSONObject(index).getDouble("longitude");
						LatLng destiny = new LatLng(latitude, longitude); // Your
																			// destiny
																			// LatLng
																			// object

						String uri = "google.navigation:q=%f, %f";
						Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, uri, destiny.latitude, destiny.longitude)));
						// if (canHandleIntent(this, navIntent))
						startActivity(navIntent);
						// else
						// Toast.makeText(this,
						// "Please install Google Navigation",
						// Toast.LENGTH_LONG).show();

						// Intent intent = new
						// Intent(android.content.Intent.ACTION_VIEW,
						// Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
						// startActivity(intent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				SelectItem(position - 1);
			}

			// else if (position == 1) {
			// final String dir =
			// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			// + "/picFolder/";
			// File newdir = new File(dir);
			// newdir.mkdirs();
			//
			// String file = dir + ".jpg";
			// File newfile = new File(file);
			// try {
			// newfile.createNewFile();
			// } catch (IOException e) {
			// }
			//
			// Uri outputFileUri = Uri.fromFile(newfile);
			//
			// Intent cameraIntent = new
			// Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			//
			// startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
			// }

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
			Log.d("CameraDemo", "Pic saved");

		}
	}

	@Override
	public void hasResponse(String response) {
		Log.d("response ", response);

	}

}

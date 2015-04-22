package com.nattysoft.navigationdrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nattysoft.navigationdrawer.listener.RequestResponseListener;
import com.nattysoft.navigationdrawer.net.CommunicationHandler;
import com.nattysoft.navigationdrawer.net.CommunicationHandler.Action;
import com.nattysoft.navigationdrawer.util.Preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentOne extends Fragment implements LocationListener {

	ImageView ivIcon;
	TextView tvItemName;

	public static final String IMAGE_RESOURCE_ID = "iconResourceID";
	public static final String ITEM_NAME = "itemName";

	private TextView addressTV;
	private Button submit_btn;
	private Button skip_btn;
	private EditText meaterReading;
	private int index = 0;
	private int pos;
	private String houseId;
	private String zoneId;
	protected boolean suspicious = false;
	private String address;
	private Activity mActivity;
	private JSONArray housesArray = null;
	private List<String> usedIndexesList = new ArrayList<String>();
	private boolean finished = false;

	private LocationManager locationManager;
	private String provider;

	public FragmentOne() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_layout_one, container, false);

		Bundle args = getArguments();
		if (args != null) {
			pos = args.getInt("position");
		}

		JSONObject jsonObject = null;
		String zoneTitle = null;
		try {
			jsonObject = new JSONObject(Preferences.getPreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON + ":" + pos));
			housesArray = jsonObject.getJSONArray("houses");
			zoneTitle = Preferences.getPreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_TITLE + ":" + pos);
			Preferences.savePreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONE_HOUSES + zoneTitle + pos, housesArray.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (Preferences.getPreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + pos) != null)
			index = Integer.parseInt(Preferences.getPreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + pos));
		if (Preferences.getPreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_USED_INDEXS + ":" + pos) != null) {
			String usedIndexes = Preferences.getPreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_USED_INDEXS + ":" + pos);
			usedIndexes = usedIndexes.replace("[", "");
			usedIndexes = usedIndexes.replace("]", "");
			usedIndexes = usedIndexes.replaceAll(" ", "");
			usedIndexesList = new ArrayList<String>(Arrays.asList(usedIndexes.split(",")));
			int count = 0;
			boolean condition = false;
			do {
				condition = false;
				count++;
				if (index >= housesArray.length() && usedIndexesList.size() == housesArray.length()) {
					finished = true;
					condition = false;
				} else if (usedIndexesList.contains(index + "")) {
					if (index < housesArray.length())
						index++;
					else
						index = 0;
					condition = true;
				} else if (count == housesArray.length()) {
					finished = true;
					condition = false;
				}

			} while (condition);

			if (finished) {
				int zonesCount = Integer.parseInt(Preferences.getPreference(mActivity, AppConstants.PreferenceKeys.ZONES_COUNT));
				Preferences.savePreference(mActivity, AppConstants.PreferenceKeys.ZONES_COUNT, "" + (zonesCount - 1));
				Preferences.deletePreference(mActivity, AppConstants.PreferenceKeys.ZONES_TITLE + ":" + pos);
				Preferences.deletePreference(mActivity, AppConstants.PreferenceKeys.ZONES_JSON + ":" + pos);
				Preferences.deletePreference(mActivity, AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + pos);
				Preferences.deletePreference(mActivity, AppConstants.PreferenceKeys.ZONES_HOUSES_USED_INDEXS + ":" + pos);
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
				alertDialogBuilder.setMessage("You have finished capturing houses in " + zoneTitle + ". Go back to Zones");
				alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent startMeterNumberCapture = new Intent(mActivity, ZonesActivity.class);
						startActivity(startMeterNumberCapture);
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
			Log.d("usedIndexes", usedIndexes);

		} else {
			if (index >= housesArray.length() - 1) {
				index = 0;
			}
		}

		meaterReading = (EditText) view.findViewById(R.id.meter_meter_reading);
		submit_btn = (Button) view.findViewById(R.id.button_submit);

		submit_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == submit_btn) {

					// action = Action.GET_ALL_OPEN_INCIDENCES;
					if (meaterReading.getText().length() > 0) {
						try {
							int lastReading = housesArray.getJSONObject(index).getInt("lastReading");
							double latitude = housesArray.getJSONObject(index).getDouble("latitude");
							double longitude = housesArray.getJSONObject(index).getDouble("longitude");

							Location locationA = new Location("point A");

							locationA.setLatitude(latitude);
							locationA.setLongitude(longitude);

							// Get the location manager
							locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
							// Define the criteria how to select the locatioin
							// provider -> use
							// default
							Criteria criteria = new Criteria();
							provider = locationManager.getBestProvider(criteria, false);
							Location location = locationManager.getLastKnownLocation(provider);
							float distance = 0;
							if (location != null) {
								distance = location.distanceTo(locationA);
							}
							if (Integer.parseInt(meaterReading.getText().toString()) <= lastReading) {
									suspicious = true;
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
									alertDialogBuilder.setMessage("Issue with captured meter reading. would you like to proceed or recapture meter reading ?");
									alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											CommunicationHandler.houseReading(mActivity, (RequestResponseListener) mActivity, houseId, zoneId, meaterReading.getText().toString(), suspicious, ProgressDialog.show(mActivity, "Please wait", "Saving a reading for " + address + "..."));
											usedIndexesList.add("" + index);
											index++;
											Preferences.savePreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + pos, "" + index);
											Preferences.savePreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_USED_INDEXS + ":" + pos, "" + usedIndexesList);
											meaterReading.setText("");
											mActivity.recreate();
										}
									});
									alertDialogBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});

									AlertDialog alertDialog = alertDialogBuilder.create();
									alertDialog.show();
							} else if (distance > 200) {
								suspicious = true;
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
								alertDialogBuilder.setMessage("You are not within radius proximity. Back  or Continue");
								alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										CommunicationHandler.houseReading(mActivity, (RequestResponseListener) mActivity, houseId, zoneId, meaterReading.getText().toString(), suspicious, ProgressDialog.show(mActivity, "Please wait", "Saving a reading for " + address + "..."));
										usedIndexesList.add("" + index);
										index++;
										Preferences.savePreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + pos, "" + index);
										Preferences.savePreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_USED_INDEXS + ":" + pos, "" + usedIndexesList);
										meaterReading.setText("");
										mActivity.recreate();
									}
								});
								alertDialogBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});

								AlertDialog alertDialog = alertDialogBuilder.create();
								alertDialog.show();
							}else {
							
								CommunicationHandler.houseReading(mActivity, (RequestResponseListener) mActivity, houseId, zoneId, meaterReading.getText().toString(), suspicious, ProgressDialog.show(mActivity, "Please wait", "Saving A Reading for " + address + "..."));
								usedIndexesList.add("" + index);
								index++;
								if (index >= housesArray.length()) {
									index = 0;
								}
								Preferences.savePreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + pos, "" + index);
								Preferences.savePreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_USED_INDEXS + ":" + pos, "" + usedIndexesList);
								meaterReading.setText("");
								mActivity.recreate();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
						alertDialogBuilder.setMessage("please capture meter reading first");
						alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();

					}
				}

			}
		});
		skip_btn = (Button) view.findViewById(R.id.button_skip);
		skip_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
				alertDialogBuilder.setTitle("Skip Reason");

				final EditText input = new EditText(mActivity);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				input.setLayoutParams(lp);
				alertDialogBuilder.setView(input);

				alertDialogBuilder.setPositiveButton("Skip", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						index++;
						Preferences.savePreference(FragmentOne.this.getActivity().getApplicationContext(), AppConstants.PreferenceKeys.ZONES_HOUSES_INDEX + ":" + pos, "" + index);
						meaterReading.setText("");
						mActivity.recreate();
					}
				});
				alertDialogBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

			}

		});
		addressTV = (TextView) view.findViewById(R.id.address_text);

		try {
			address = housesArray.getJSONObject(index).getString("address");
			houseId = housesArray.getJSONObject(index).getString("id");
			zoneId = jsonObject.getString("id");
			addressTV.setText(address);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the location manager
		locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {
			// latituteField.setText("Location not available");
			// longitudeField.setText("Location not available");
		}

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	/* Request updates at startup */
	@Override
	public void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		double lat = (double) (location.getLatitude());
		double lng = (double) (location.getLongitude());
		// latituteField.setText(String.valueOf(lat));
		// longitudeField.setText(String.valueOf(lng));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(mActivity, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(mActivity, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();

	}

}
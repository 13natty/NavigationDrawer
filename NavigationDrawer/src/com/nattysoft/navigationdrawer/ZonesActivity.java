package com.nattysoft.navigationdrawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.fo;
import com.nattysoft.navigationdrawer.push.GCMBroadcastReceiver;
import com.nattysoft.navigationdrawer.AppConstants;
import com.nattysoft.navigationdrawer.util.Preferences;
import com.nattysoft.navigationdrawer.listener.PushListener;
import com.nattysoft.navigationdrawer.listener.RequestResponseListener;
import com.nattysoft.navigationdrawer.net.CommunicationHandler;
import com.nattysoft.navigationdrawer.net.CommunicationHandler.Action;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ZonesActivity extends Activity implements RequestResponseListener, PushListener {
	public static Action action;
	ListView listView;
	private String[] values = new String[] {};
	ArrayList<HashMap<String, String>> zoneslist = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Preferences.getPreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.KEY_SERVER_URL) == null) {
			Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.KEY_SERVER_URL, AppConstants.Config.SERVER_URL);
		}

		setContentView(R.layout.activity_zones);

		// Get ListView object from xml
		listView = (ListView) findViewById(R.id.list);

		int zonesCount = Preferences.getPreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT)==null?0:Integer.parseInt(Preferences.getPreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT));
		if(zonesCount>0)
		{
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				String zone = extras.getString("body");
				JSONObject jsonBody;
				try {
					jsonBody = new JSONObject(zone);
					String name = jsonBody.getString("name");
					boolean saved = false;
					for (int i = 0; i < zonesCount; i++) {
						if (jsonBody.getString("id").equalsIgnoreCase(Preferences.getPreference(getBaseContext(), new JSONObject(AppConstants.PreferenceKeys.ZONES_JSON + ":" + i).getString("id")))) {
							Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT, "" + (i + 1));
							Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_TITLE + ":" + i, name);
							Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON + ":" + i, zone);
							saved = true;
						}
					}
					if(!saved)
					{
						Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT, "" + (zonesCount + 1));
						Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_TITLE + ":" + zonesCount, name);
						Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON + ":" + zonesCount, zone);
					}
					zonesCount++;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			values = new String[zonesCount];
			
			for (int i = 0; i < values.length; i++) {
				values[i] = Preferences.getPreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_TITLE+":"+i);
			}
		}else
		{
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				String zone = extras.getString("body");
				JSONObject jsonBody;
				try {
					jsonBody = new JSONObject(zone);
					String name = jsonBody.getString("name");
					Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT, "1");
					Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_TITLE+":"+0, name);
					Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON + ":" + 0, zone);
					values = new String[1];
					values[values.length - 1] = name;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		ZonesAdapeter adapter = new ZonesAdapeter(this, values);

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
				Toast.makeText(getApplicationContext(), "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG).show();

				Intent startMeterNumberCapture = new Intent(ZonesActivity.this, MeterNumberCapture.class);
				startMeterNumberCapture.putExtra("title", itemValue);
				startMeterNumberCapture.putExtra("position", position);
				startActivity(startMeterNumberCapture);

			}

		});

		CommunicationHandler.registerUser(this.getApplicationContext(), (RequestResponseListener) this);
		GCMBroadcastReceiver.pushListener = this;
	}

	@Override
	public void hasResponse(String response) {
		// TODO Auto-generated method stub
		Log.d("response ", response);
	}

	class ZonesAdapeter extends ArrayAdapter<String> {

		Context context;
		String[] values;
		private View row;

		public ZonesAdapeter(Context context, String[] values) {
			super(context, R.layout.zone_item, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.zone_item, parent, false);
			TextView desc = (TextView) row.findViewById(R.id.zone_text);

			desc.setText(values[position]);

			return row;
		}

		@Override
		public int getCount() {
			int listSize = values.length;
			int count = super.getCount();
			int productlistC = values.length;
			Log.d("inside getCount", "productlist.size() " + listSize);
			Log.d("inside getCount", "superC " + count);
			Log.d("inside getCount", "productlistC " + productlistC);
			if (count < 1) {
				return productlistC;
			}

			return count;
		}
	}

	@Override
	public void pushReceived(Context context, Intent intent) {
		final Bundle extras = intent.getExtras();
		String message = extras.getString("body");
		String name = "";
		if (isAppRunning(context)) {

			try {
				JSONObject jsonBody = new JSONObject(message);
				name = jsonBody.getString("name");
				int zonesCount = Preferences.getPreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT)==null?0:Integer.parseInt(Preferences.getPreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT));
				boolean saved = false;
				for (int i = 0; i < zonesCount; i++) {
					if(jsonBody.getString("id").equalsIgnoreCase(Preferences.getPreference(context, new JSONObject(Preferences.getPreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON+":"+i)).getString("id"))))
					{
						Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT, ""+(i+1));
						Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_TITLE+":"+i, name);
						Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON+":"+i, message);
						saved = true;
					}
				}
				if(!saved)
				{
					Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_COUNT, ""+(zonesCount+1));
					Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_TITLE+":"+zonesCount, name);
					Preferences.savePreference(ZonesActivity.this.getApplicationContext(), AppConstants.PreferenceKeys.ZONES_JSON+":"+zonesCount, message);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			Intent refresh = new Intent(this, ZonesActivity.class);
			startActivity(refresh);
			this.finish();

		} else {
			Intent myIntent = new Intent(context, ZonesActivity.class);
			Bundle extrasBundle = new Bundle();
			try {
				JSONObject jsonBody = new JSONObject(message);
				name = jsonBody.getString("name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			extrasBundle.putString("body", message);
			myIntent.putExtras(extrasBundle);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FLAG_ACTIVITY_CLEAR_TASK);

			// Build notification
			// Actions are just fake
			Notification noti = new NotificationCompat.Builder(this).setContentTitle("Zone Received ").setContentText(name).setSmallIcon(R.drawable.icon_push).setWhen(System.currentTimeMillis()).setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND).setDefaults(Notification.DEFAULT_VIBRATE).setDefaults(Notification.DEFAULT_LIGHTS).setAutoCancel(true).build();
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// hide the notification after its selected
			noti.flags |= Notification.FLAG_AUTO_CANCEL;

			notificationManager.notify(0, noti);
		}
	}

	// foreground---

	/**
	 * Check if the android application is being sent in the background (i.e
	 * behind another application's Activity).
	 * 
	 * @param context
	 *            the context
	 * @return true if another application will be above this one.
	 */
	public static boolean isAppRunning(Context context) {
		// check with the first task(task in the foreground)
		// in the returned list of tasks
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
		if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString())) {
			// your application is running in the background
			return true;
		}
		return false;
	}

}
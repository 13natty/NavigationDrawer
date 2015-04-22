package com.nattysoft.navigationdrawer.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.util.Pair;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.nattysoft.navigationdrawer.AppConstants;
import com.nattysoft.navigationdrawer.listener.RequestResponseListener;
import com.nattysoft.navigationdrawer.net.CommunicationHandler;
import com.nattysoft.navigationdrawer.net.ConnectionManager;
import com.nattysoft.navigationdrawer.push.GCMer;
import com.nattysoft.navigationdrawer.util.Preferences;

public class CommunicationHandler {
	private static final String LOG_TAG = CommunicationHandler.class.getSimpleName();

	public enum Action {
		GET_ALL_OPEN_INCIDENCES, REGISTER, ACCEPT_INCIDENT, DECLINE_INCIDENT, ADD_COMMENT, GET_COMMENTS, SEND_CHAT, UPDATE_JOB_CARD, SAVE_JOB_CARD, INCIDENT_STATUS, GET_ALL_OPEN_INCIDENCES_BG;
	}

	public static void registerForPush(final Context context, String deviceId, String employeeNumber, RequestResponseListener listener, ProgressDialog dialog) {

		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "registerdevice.mobi");
			json.accumulate("registrationId", deviceId);
			json.accumulate("employeeNum", employeeNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "json.toString() " + json.toString());

		new ConnectionManager().post(context, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));
	}

	public static void getOpenIncidents(Context context, RequestResponseListener listener, ProgressDialog dialog) {

		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "savehouse.mobi");
			json.accumulate("houseId", 1);
			json.accumulate("zoneId", 1);
			json.accumulate("timeread", "703423423423");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "nameValuePairs.toString() " + json.toString());
		new ConnectionManager().post(context, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));
	}

	public static void registerUser(Context context, RequestResponseListener listener, ProgressDialog dialog) {

		GCMer.onCreate(context, listener, dialog);

	}
	
	public static void registerUser(Context context, RequestResponseListener listener) {

		GCMer.onCreate(context, listener);

	}

	public static void acceptIncident(Context context, RequestResponseListener listener, ProgressDialog dialog, String employeeNum, String incidentId) {
		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "acceptincident.mobi");
			json.accumulate("employeeNum", employeeNum);
			json.accumulate("incidentId", incidentId);
			json.accumulate("accept", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "nameValuePairs.toString() " + json.toString());
		new ConnectionManager().post(context, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));

	}

	public static void declineIncident(Context context, RequestResponseListener listener, ProgressDialog dialog, String employeeNum, String incidentId, String reason) {
		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "acceptincident.mobi");
			json.accumulate("employeeNum", employeeNum);
			json.accumulate("incidentId", incidentId);
			json.accumulate("accept", false);
			json.accumulate("declineReason", reason);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "nameValuePairs.toString() " + json.toString());
		new ConnectionManager().post(context, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));

	}

	public static void addComment(Activity activity, RequestResponseListener listener, ProgressDialog dialog, String employeeNum, String incidentId, String message) {
		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "addincidentcomment.mobi");
			json.accumulate("employeeNum", employeeNum);
			json.accumulate("incidentId", incidentId);
			json.accumulate("comment", message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(activity, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "nameValuePairs.toString() " + json.toString());
		new ConnectionManager().post(activity, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(activity, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));

	}

//	

	private static boolean isValid(String string) {
		if (string != null && !string.isEmpty()) {
			return true;
		}

		return false;
	}

	public static void getComments(Activity activity, RequestResponseListener listener, ProgressDialog dialog, String incidentId) {
		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "getincidentcomments.mobi");
			json.accumulate("incidentId", incidentId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(activity, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "nameValuePairs.toString() " + json.toString());
		new ConnectionManager().post(activity, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(activity, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));

	}

	public static void sendChat(Context context, RequestResponseListener listener, ProgressDialog dialog, String message, String employeeNum) {
		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "sendmessage.mobi");
			json.accumulate("body", message);
			json.accumulate("senderEmployNum", employeeNum);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "nameValuePairs.toString() " + json.toString());
		new ConnectionManager().post(context, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));

	}

	

	public static void pingIncidentReceived(Context context, RequestResponseListener listener, ProgressDialog dialog, String incidentId) {
		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "incidentreceived.mobi");
			json.accumulate("employeeNum", Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_EMPLOYEE_NUM));
			json.accumulate("incidentId", incidentId); 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "nameValuePairs.toString() " + json.toString());
		new ConnectionManager().post(context, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));
	}

	public static void houseReading(Context context, RequestResponseListener listener, String houseId, String zoneId, String meterReading, boolean suspicious, ProgressDialog dialog) {
		JSONObject json = new JSONObject();
		try {
			json.accumulate("nav", "savehouse.mobi");
			json.accumulate("houseId", houseId);
			json.accumulate("zoneId", zoneId);
			json.accumulate("timeread", System.currentTimeMillis());
			json.accumulate("meterReading", meterReading);
			json.accumulate("suspicious", suspicious);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "SERVER_URL " + Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL));
		Log.d(LOG_TAG, "nameValuePairs.toString() " + json.toString());
		new ConnectionManager().post(context, listener, dialog, new Pair<String, JSONObject>(Preferences.getPreference(context, AppConstants.PreferenceKeys.KEY_SERVER_URL), json));
	}

}